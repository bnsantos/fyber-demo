package com.bnsantos.fyber.exceptions;

import com.bnsantos.fyber.model.BadRequest;

/**
 * Created by bruno on 21/11/15.
 */
public class BadRequestException extends RuntimeException {
    private final BadRequest badRequest;

    public BadRequestException(String detailMessage, BadRequest badRequest) {
        super(detailMessage);
        this.badRequest = badRequest;
    }

    public BadRequest getBadRequest() {
        return badRequest;
    }
}
