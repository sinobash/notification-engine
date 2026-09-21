package com.sinobash.api.notification_engine.shared.error;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String message)
    {
        super(message);
    }
}
