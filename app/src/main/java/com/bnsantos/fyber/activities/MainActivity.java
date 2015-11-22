package com.bnsantos.fyber.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bnsantos.fyber.App;
import com.bnsantos.fyber.Constants;
import com.bnsantos.fyber.R;
import com.bnsantos.fyber.recyclerview.RecyclerViewOnTouchListener;
import com.bnsantos.fyber.adapters.OffersAdapter;
import com.bnsantos.fyber.exceptions.BadRequestException;
import com.bnsantos.fyber.exceptions.InvalidResponseException;
import com.bnsantos.fyber.model.Offer;
import com.bnsantos.fyber.model.OfferResponse;
import com.bnsantos.fyber.provider.OfferProvider;
import com.bnsantos.fyber.provider.RequestUtil;
import com.trello.rxlifecycle.ActivityEvent;
import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import java.util.ArrayList;
import java.util.List;

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
    private int lastLoadedPage = 1;

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

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        adapter = new OffersAdapter(new ArrayList<Offer>(), this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnItemTouchListener(new RecyclerViewOnTouchListener(this, new RecyclerViewOnTouchListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Offer offer = adapter.getItem(position);
                if (offer != null) {
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(offer.getLink()));
                    if (i.resolveActivity(getPackageManager()) != null) {
                        startActivity(i);
                    } else {
                        showToastMessage(R.string.error_no_app_open_link);
                    }
                } else {
                    fetchOffers(false, lastLoadedPage + 1);
                }
            }
        }));

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchOffers(true, 1);
            }
        });

        provider = ((App) getApplication()).getProvider();

        fetchOffers(true, 1);
    }

    private void extractIntentData(Intent intent) {
        if (intent != null) {
            uid = intent.getStringExtra(Constants.EXTRA_UID);
            apiKey = intent.getStringExtra(Constants.EXTRA_API_KEY);
            appId = intent.getStringExtra(Constants.EXTRA_APP_ID);
            pub0 = intent.getStringExtra(Constants.EXTRA_PUB0);
        }
    }

    private void fetchOffers(final boolean clear, int page) {
        lastLoadedPage = page;
        swipeRefreshLayout.setRefreshing(true);
        provider.getOffers(RequestUtil.generateQueryParams(this, appId, uid, pub0, "109.235.143.113", "de", apiKey, page))
                .compose(this.<Response>bindUntilEvent(ActivityEvent.DESTROY))
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.newThread())
                .subscribe(new Action1<Response>() {
                    @Override
                    public void call(Response response) {
                        OfferResponse offerResponse = RequestUtil.extractAndValidateResponse(((App) getApplication()).getGson(), response, apiKey);
                        if (offerResponse != null && offerResponse.getOffers() != null) {
                            updateOffersUiThread(clear, offerResponse.getOffers());
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

    private void updateOffersUiThread(final boolean clear, final List<Offer> offerList) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.addAll(offerList, clear);
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
