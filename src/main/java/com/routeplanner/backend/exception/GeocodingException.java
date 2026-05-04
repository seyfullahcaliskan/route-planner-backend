package com.routeplanner.backend.exception;

public class GeocodingException extends RuntimeException {
    public GeocodingException(String address) {
        super(String.format("Adres çözümlenemedi: %s", address));
    }

    public GeocodingException(String address, Throwable cause) {
        super(String.format("Adres çözümlenemedi: %s", address), cause);
    }
}