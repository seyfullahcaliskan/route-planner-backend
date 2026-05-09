package com.routeplanner.backend.exception;

/**
 * Tüm backend hataları için merkezi kod kataloğu.
 *
 * Frontend bu kodlara bakar, errorCode → i18n key olarak mapler.
 * "message" alanı sadece fallback olarak (kod tanınmazsa) kullanılır.
 *
 * Yeni kod eklerken: kod isimlendirmesi UPPER_SNAKE_CASE, anlamlı ve dar kapsamlı.
 */
public enum ApiErrorCode {

    // -------- Validation / generic --------
    VALIDATION_ERROR,
    INVALID_REQUEST,
    BAD_CREDENTIALS,

    // -------- Auth --------
    EMAIL_ALREADY_EXISTS,
    OAUTH_VERIFICATION_FAILED,
    UNAUTHORIZED,
    REFRESH_TOKEN_INVALID,

    // -------- User profile --------
    USER_NOT_FOUND,
    PASSWORD_INCORRECT,
    PASSWORD_TOO_SHORT,
    PASSWORD_REQUIRED_FOR_OAUTH_USER,
    EMAIL_INVALID,

    // -------- Saved place --------
    PLACE_NOT_FOUND,
    PLACE_FORBIDDEN,           // başka kullanıcının yerine erişim denemesi
    PLACE_NAME_REQUIRED,
    PLACE_ADDRESS_REQUIRED,
    PLACE_COORDS_REQUIRED,
    PLACE_DEFAULT_CONFLICT,    // 2'den fazla varsayılan başlangıç/bitiş

    // -------- Route / stop --------
    ROUTE_NOT_FOUND,
    ROUTE_FORBIDDEN,
    STOP_NOT_FOUND,
    GEOCODING_FAILED,
    OPTIMIZATION_FAILED,

    // -------- Plan --------
    PLAN_LIMIT_EXCEEDED,

    // -------- Server --------
    INTERNAL_ERROR;
}
