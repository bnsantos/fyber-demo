package com.bnsantos.fyber.exceptions;

/**
 * Created by bruno on 21/11/15.
 */
public class InvalidResponseException extends RuntimeException {
    private final int string;

    public InvalidResponseException(String detailMessage, int message) {
        super(detailMessage);
        this.string = message;
    }

    public int getResourceMessage() {
        return string;
    }
}
