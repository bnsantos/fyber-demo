package com.bnsantos.fyber.service;


import java.util.Map;

import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.QueryMap;
import rx.Observable;

/**
 * Created by bruno on 21/11/15.
 */
public interface OfferService {
    @GET("/feed/v1/offers.json")
    Observable<Response> getOffers(@QueryMap Map<String, String> options);
}
