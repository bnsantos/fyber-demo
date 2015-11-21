package com.bnsantos.fyber.model;

import java.util.List;

/**
 * Created by bruno on 21/11/15.
 */
public class OfferResponse {
    private String code;
    private String message;
    private int count;
    private int pages;
    private Information information;
    private List<Offer> offers;

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public int getCount() {
        return count;
    }

    public int getPages() {
        return pages;
    }

    public Information getInformation() {
        return information;
    }

    public List<Offer> getOffers() {
        return offers;
    }
}
