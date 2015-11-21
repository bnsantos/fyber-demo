package com.bnsantos.fyber.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.bnsantos.fyber.App;
import com.bnsantos.fyber.Constants;
import com.bnsantos.fyber.R;
import com.bnsantos.fyber.adapters.OffersAdapter;
import com.bnsantos.fyber.exceptions.BadRequestException;
import com.bnsantos.fyber.exceptions.InvalidResponseException;
import com.bnsantos.fyber.model.BadRequest;
import com.bnsantos.fyber.model.Offer;
import com.bnsantos.fyber.model.OfferResponse;
import com.bnsantos.fyber.provider.OfferProvider;
import com.google.common.base.Charsets;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.google.common.io.Closeables;
import com.trello.rxlifecycle.ActivityEvent;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.client.Header;
import retrofit.client.Response;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class MainActivity extends RxAppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private String uid;
    private String apiKey;
    private String appId;
    private String pub0;

    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private OffersAdapter adapter;

    private OfferProvider provider;
    private HashFunction hashFunction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        extractBundleInfo(savedInstanceState);
        setContentView(R.layout.activity_main);
        extractIntentData(getIntent());
        if (uid == null || apiKey == null || appId == null) {
            Toast.makeText(this, R.string.error_cant_get_offers, Toast.LENGTH_SHORT).show();
            finish();
        }

        hashFunction = Hashing.sha1();

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        adapter = new OffersAdapter(new ArrayList<Offer>());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                reload();
            }
        });

        provider = ((App) getApplication()).getProvider();
    }

    private void extractIntentData(Intent intent) {
        if (intent != null) {
            uid = intent.getStringExtra(Constants.EXTRA_UID);
            apiKey = intent.getStringExtra(Constants.EXTRA_API_KEY);
            appId = intent.getStringExtra(Constants.EXTRA_APP_ID);
            pub0 = intent.getStringExtra(Constants.EXTRA_PUB0);
        }
    }

    private void reload() {
        swipeRefreshLayout.setRefreshing(true);
        provider.getOffers(generateQueryParams(0))
                .compose(this.<Response>bindUntilEvent(ActivityEvent.DESTROY))
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.newThread())
                .subscribe(new Action1<Response>() {
                    @Override
                    public void call(Response response) {
                        OfferResponse offerResponse = validateResponse(response);
                        if (offerResponse != null && offerResponse.getOffers() != null) {
                            updateOffersUiThread(offerResponse.getOffers());
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.i(TAG, "Error get offers", throwable);
                        if (throwable instanceof BadRequestException) {
                            showToastMessage(((BadRequestException) throwable).getBadRequest().getMessage());
                        } else if (throwable instanceof InvalidResponseException) {
                            showToastMessage(((InvalidResponseException) throwable).getResourceMessage());
                        } else {
                            showToastMessage(R.string.error_unknown);
                        }

                    }
                }, new Action0() {
                    @Override
                    public void call() {
                        Log.i(TAG, "Finished get offers");
                    }
                });
    }

    private Map<String, String> generateQueryParams(int page) {
        Map<String, String> params = new HashMap<>();
        params.put(Constants.PARAM_APP_ID, appId);
        params.put(Constants.PARAM_DEVICE_ID, App.deviceId(this));
        params.put(Constants.PARAM_IP, "109.235.143.113"); //TODO constants
        params.put(Constants.PARAM_LOCALE, "de");
        params.put(Constants.PARAM_PAGE, Integer.toString(page));
        if (pub0 != null && pub0.length() > 0) {
            params.put(Constants.PARAM_PUB0, pub0);
        }
        params.put(Constants.PARAM_TIMESTAMP, Long.toString(System.currentTimeMillis() / 1000));
        params.put(Constants.PARAM_USER_ID, uid);
        params.put(Constants.PARAM_HASH_KEY, generateHashKey(params));
        return params;
    }

    private String generateHashKey(Map<String, String> params) {
        StringBuilder hashString = new StringBuilder();
        List<String> keys = new ArrayList<>(params.keySet());
        Collections.sort(keys);
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            hashString.append(key + "=" + params.get(key) + "&");

        }
        hashString.append(apiKey);
        return hashFunction.hashString(hashString.toString(), Charsets.UTF_8).toString();
    }

    private void extractBundleInfo(Bundle savedState) {
        if (savedState != null) {
            uid = savedState.getString(Constants.EXTRA_UID);
            apiKey = savedState.getString(Constants.EXTRA_API_KEY);
            appId = savedState.getString(Constants.EXTRA_APP_ID);
            pub0 = savedState.getString(Constants.EXTRA_PUB0);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(Constants.EXTRA_UID, uid);
        outState.putString(Constants.EXTRA_API_KEY, apiKey);
        outState.putString(Constants.EXTRA_APP_ID, appId);
        outState.putString(Constants.EXTRA_PUB0, pub0);
    }

    private OfferResponse validateResponse(Response response) {
        if (response.getStatus() == 200) {
            String headerHash = extractResponseHeaderValidator(response);
            if (TextUtils.isEmpty(headerHash)) {
                throw new InvalidResponseException("No header validator on response", R.string.error_invalid_response);
            }
            String body = extractBody(response);
            if (!TextUtils.isEmpty(body)) {
                String bodyHash = hashFunction.hashString(body + apiKey, Charsets.UTF_8).toString();
                if (bodyHash.equals(headerHash)) {
                    return ((App) getApplication()).getGson().fromJson(body, OfferResponse.class);
                } else {
                    throw new InvalidResponseException("Response hash does not match", R.string.error_invalid_response);
                }
            } else {
                return null;
            }
        } else {
            String body = extractBody(response);
            throw new BadRequestException("Bad Request", ((App) getApplication()).getGson().fromJson(body, BadRequest.class));
        }
    }

    private String extractResponseHeaderValidator(Response response) {
        List<Header> headers = response.getHeaders();
        for (int i = 0; i < headers.size(); i++) {
            Header header = headers.get(i);
            if (Constants.VALIDATE_HEADER.equals(header.getName())) {
                return header.getValue();
            }
        }
        return null;
    }

    private String extractBody(Response response) {
        StringBuilder sb = new StringBuilder();
        InputStream in = null;
        try {
            in = response.getBody().in();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            Closeables.closeQuietly(in);
        }
        return sb.toString();
    }

    private void updateOffersUiThread(final List<Offer> offerList) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.addAll(offerList);
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void showToastMessage(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showToastMessage(final int message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
