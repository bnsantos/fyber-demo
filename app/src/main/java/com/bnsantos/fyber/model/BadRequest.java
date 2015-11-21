package com.bnsantos.fyber.model;

/**
 * Created by bruno on 21/11/15.
 */
public class BadRequest {
    private String code;
    private String message;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
