package com.bnsantos.fyber;

import android.app.Application;
import android.content.Context;
import android.provider.Settings;

import com.bnsantos.fyber.provider.OfferProvider;
import com.bnsantos.fyber.service.OfferService;
import com.google.gson.Gson;

import retrofit.RestAdapter;


/**
 * Created by bruno on 21/11/15.
 */
public class App extends Application {
    private OfferProvider provider;
    private Gson gson;

    @Override
    public void onCreate() {
        super.onCreate();
        gson = new Gson();
        provider = initOfferProvider();
    }

    public OfferProvider getProvider() {
        return provider;
    }

    public Gson getGson() {
        return gson;
    }

    protected RestAdapter initRetrofit() {
        return new RestAdapter.Builder()
                .setEndpoint(Constants.BASE_URL)
                .build();
    }

    protected OfferProvider initOfferProvider() {
        return new OfferProvider(initRetrofit().create(OfferService.class));
    }

    public static String deviceId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }
}
