package com.hotel_app.exception;

import java.sql.SQLException;

public class InternalServerException extends SQLException {

    public InternalServerException(String message) {
        super(message);
    }
}