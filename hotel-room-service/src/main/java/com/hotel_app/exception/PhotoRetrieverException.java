package com.hotel_app.exception;

public class PhotoRetrieverException extends RuntimeException {
    public PhotoRetrieverException(String reason) {
        super(reason);
    }
}