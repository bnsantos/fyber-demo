package com.bnsantos.fyber.model;

/**
 * Created by bruno on 21/11/15.
 */
public class Offer {
    private String link;
    private String title;
    private long offer_id;
    private String teaser;
    private String required_actions;
    private Thumbnail thumbnail;

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getOffer_id() {
        return offer_id;
    }

    public void setOffer_id(long offer_id) {
        this.offer_id = offer_id;
    }

    public String getTeaser() {
        return teaser;
    }

    public void setTeaser(String teaser) {
        this.teaser = teaser;
    }

    public String getRequired_actions() {
        return required_actions;
    }

    public void setRequired_actions(String required_actions) {
        this.required_actions = required_actions;
    }

    public Thumbnail getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(Thumbnail thumbnail) {
        this.thumbnail = thumbnail;
    }
}
