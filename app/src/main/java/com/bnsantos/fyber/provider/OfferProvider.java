package com.bnsantos.fyber.provider;

import com.bnsantos.fyber.service.OfferService;

import java.util.Map;

import retrofit.client.Response;
import rx.Observable;

/**
 * Created by bruno on 21/11/15.
 */
public class OfferProvider {
    private final OfferService service;

    public OfferProvider(OfferService service) {
        this.service = service;
    }

    public Observable<Response> getOffers(Map<String, String> options) {
        return service.getOffers(options);
    }
}

