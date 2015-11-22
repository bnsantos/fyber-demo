package com.bnsantos.fyber.provider;

import android.content.Context;
import android.text.TextUtils;

import com.bnsantos.fyber.App;
import com.bnsantos.fyber.Constants;
import com.bnsantos.fyber.R;
import com.bnsantos.fyber.exceptions.BadRequestException;
import com.bnsantos.fyber.exceptions.InvalidResponseException;
import com.bnsantos.fyber.model.BadRequest;
import com.bnsantos.fyber.model.OfferResponse;
import com.google.common.base.Charsets;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.google.common.io.Closeables;
import com.google.gson.Gson;

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

/**
 * Created by bruno on 22/11/15.
 */
public class RequestUtil {
    private static HashFunction hashFunction = Hashing.sha1();

    public Map<String, String> generateQueryParams(Context context, String appId, String uid, String pub0, String ip, String locale, String apiKey, int page) {
        Map<String, String> params = new HashMap<>();
        params.put(Constants.PARAM_APP_ID, appId);
        params.put(Constants.PARAM_DEVICE_ID, App.deviceId(context));
        params.put(Constants.PARAM_IP, ip);
        params.put(Constants.PARAM_LOCALE, locale);
        params.put(Constants.PARAM_PAGE, Integer.toString(page));
        if (pub0 != null && pub0.length() > 0) {
            params.put(Constants.PARAM_PUB0, pub0);
        }
        params.put(Constants.PARAM_TIMESTAMP, Long.toString(System.currentTimeMillis() / 1000));
        params.put(Constants.PARAM_USER_ID, uid);
        params.put(Constants.PARAM_HASH_KEY, generateHashKey(params, apiKey));
        return params;
    }

    private String generateHashKey(Map<String, String> params, String apiKey) {
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

    public OfferResponse extractAndValidateResponse(Gson gson, Response response, String apiKey) {
        if (response.getStatus() == 200) {
            String headerHash = extractResponseHeaderValidator(response);
            if (TextUtils.isEmpty(headerHash)) {
                throw new InvalidResponseException("No header validator on response", R.string.error_invalid_response);
            }
            String body = extractBody(response);
            if (!TextUtils.isEmpty(body)) {
                String bodyHash = hashFunction.hashString(body + apiKey, Charsets.UTF_8).toString();
                if (bodyHash.equals(headerHash)) {
                    return gson.fromJson(body, OfferResponse.class);
                } else {
                    throw new InvalidResponseException("Response hash does not match", R.string.error_invalid_response);
                }
            } else {
                return null;
            }
        } else {
            String body = extractBody(response);
            throw new BadRequestException("Bad Request", gson.fromJson(body, BadRequest.class));
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
}
