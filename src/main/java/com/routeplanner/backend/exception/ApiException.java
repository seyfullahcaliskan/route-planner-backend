package com.routeplanner.backend.exception;

import org.springframework.http.HttpStatus;

/**
 * Tipli hata. Throw eden kod sadece kod + insanca mesaj sağlar;
 * GlobalExceptionHandler buradan ProblemDetail oluşturur.
 */
public class ApiException extends RuntimeException {

    private final ApiErrorCode code;
    private final HttpStatus status;

    public ApiException(ApiErrorCode code, HttpStatus status, String message) {
        super(message);
        this.code = code;
        this.status = status;
    }

    public ApiErrorCode getCode() {
        return code;
    }

    public HttpStatus getStatus() {
        return status;
    }

    // ----- Fabrika kısayolları -----

    public static ApiException badRequest(ApiErrorCode code, String message) {
        return new ApiException(code, HttpStatus.BAD_REQUEST, message);
    }

    public static ApiException unauthorized(ApiErrorCode code, String message) {
        return new ApiException(code, HttpStatus.UNAUTHORIZED, message);
    }

    public static ApiException forbidden(ApiErrorCode code, String message) {
        return new ApiException(code, HttpStatus.FORBIDDEN, message);
    }

    public static ApiException notFound(ApiErrorCode code, String message) {
        return new ApiException(code, HttpStatus.NOT_FOUND, message);
    }

    public static ApiException conflict(ApiErrorCode code, String message) {
        return new ApiException(code, HttpStatus.CONFLICT, message);
    }

    public static ApiException unprocessable(ApiErrorCode code, String message) {
        return new ApiException(code, HttpStatus.UNPROCESSABLE_ENTITY, message);
    }
}
