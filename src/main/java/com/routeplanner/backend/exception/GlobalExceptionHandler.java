package com.routeplanner.backend.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Tüm hatalar standart bir şekilde döner:
 * {
 *   "type": "/errors/<kategori>",
 *   "title": "...",
 *   "status": 400,
 *   "detail": "İnsana yönelik fallback mesaj (TR)",
 *   "errorCode": "EMAIL_ALREADY_EXISTS",
 *   "message": "İnsana yönelik fallback mesaj (TR)",
 *   "fieldErrors": { "email": "Geçersiz e-posta" },  // varsa
 *   "timestamp": "2025-..."
 * }
 *
 * Frontend "errorCode" alanına bakıp i18n dictionary'den çevirir;
 * tanınmazsa "message" fallback olarak gösterilir.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /** Tüm tipli hatalar buradan geçer. */
    @ExceptionHandler(ApiException.class)
    public ProblemDetail handleApi(ApiException ex) {
        log.debug("ApiException [{}]: {}", ex.getCode(), ex.getMessage());
        return build(
                ex.getStatus(),
                ex.getCode(),
                ex.getMessage(),
                "/errors/" + ex.getCode().name().toLowerCase().replace('_', '-')
        );
    }

    /** @Valid hataları → ilk alan hatasını öne çıkar, hepsini fieldErrors içinde döndür. */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();
        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.putIfAbsent(
                    fe.getField(),
                    fe.getDefaultMessage() != null ? fe.getDefaultMessage() : "Geçersiz değer"
            );
        }

        String firstMessage = fieldErrors.values().stream().findFirst().orElse("Geçersiz istek");

        ProblemDetail problem = build(
                HttpStatus.BAD_REQUEST,
                ApiErrorCode.VALIDATION_ERROR,
                firstMessage,
                "/errors/validation"
        );
        problem.setProperty("fieldErrors", fieldErrors);
        return problem;
    }

    /** Spring Security: hatalı şifre/kimlik. */
    @ExceptionHandler(BadCredentialsException.class)
    public ProblemDetail handleBadCredentials(BadCredentialsException ex) {
        return build(
                HttpStatus.UNAUTHORIZED,
                ApiErrorCode.BAD_CREDENTIALS,
                "E-posta veya şifre hatalı.",
                "/errors/bad-credentials"
        );
    }

    @ExceptionHandler(AuthenticationException.class)
    public ProblemDetail handleAuth(AuthenticationException ex) {
        return build(
                HttpStatus.UNAUTHORIZED,
                ApiErrorCode.UNAUTHORIZED,
                "Oturumunuz geçersiz, lütfen tekrar giriş yapın.",
                "/errors/unauthorized"
        );
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail handleAccessDenied(AccessDeniedException ex) {
        return build(
                HttpStatus.FORBIDDEN,
                ApiErrorCode.UNAUTHORIZED,
                "Bu işlem için yetkiniz yok.",
                "/errors/forbidden"
        );
    }

    /**
     * Geriye dönük uyumluluk: AuthService gibi yerlerde IllegalArgumentException
     * fırlatılıyor. Mesaj string'e bakarak en uygun kodu seçiyoruz.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleIllegalArgument(IllegalArgumentException ex) {
        String message = ex.getMessage() == null ? "" : ex.getMessage();
        ApiErrorCode code = ApiErrorCode.INVALID_REQUEST;

        String lower = message.toLowerCase();
        if (lower.contains("e-posta") && (lower.contains("zaten") || lower.contains("kayıtlı"))) {
            code = ApiErrorCode.EMAIL_ALREADY_EXISTS;
        } else if (lower.contains("şifre") || lower.contains("password")) {
            code = ApiErrorCode.BAD_CREDENTIALS;
        }

        log.debug("IllegalArgument → {}: {}", code, message);
        return build(HttpStatus.BAD_REQUEST, code, message, "/errors/business");
    }

    @ExceptionHandler(OAuthVerificationException.class)
    public ProblemDetail handleOAuth(OAuthVerificationException ex) {
        log.warn("OAuth doğrulama hatası: {}", ex.getMessage());
        return build(
                HttpStatus.UNAUTHORIZED,
                ApiErrorCode.OAUTH_VERIFICATION_FAILED,
                ex.getMessage(),
                "/errors/oauth"
        );
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ProblemDetail handleResourceNotFound(ResourceNotFoundException ex) {
        log.debug("Kaynak bulunamadı: {}", ex.getMessage());
        ApiErrorCode code = ApiErrorCode.INVALID_REQUEST;
        String msg = ex.getMessage() == null ? "Kaynak bulunamadı" : ex.getMessage();
        String lower = msg.toLowerCase();
        if (lower.contains("user")) code = ApiErrorCode.USER_NOT_FOUND;
        else if (lower.contains("routeplan") || lower.contains("route plan")) code = ApiErrorCode.ROUTE_NOT_FOUND;
        else if (lower.contains("routestop") || lower.contains("stop")) code = ApiErrorCode.STOP_NOT_FOUND;
        else if (lower.contains("place")) code = ApiErrorCode.PLACE_NOT_FOUND;

        return build(HttpStatus.NOT_FOUND, code, msg, "/errors/not-found");
    }

    @ExceptionHandler(OptimizationException.class)
    public ProblemDetail handleOptimization(OptimizationException ex) {
        log.error("Optimizasyon hatası: {}", ex.getMessage(), ex);
        return build(
                HttpStatus.UNPROCESSABLE_ENTITY,
                ApiErrorCode.OPTIMIZATION_FAILED,
                ex.getMessage(),
                "/errors/optimization"
        );
    }

    @ExceptionHandler(GeocodingException.class)
    public ProblemDetail handleGeocoding(GeocodingException ex) {
        log.warn("Geocoding hatası: {}", ex.getMessage());
        return build(
                HttpStatus.UNPROCESSABLE_ENTITY,
                ApiErrorCode.GEOCODING_FAILED,
                ex.getMessage(),
                "/errors/geocoding"
        );
    }

    @ExceptionHandler(PlanLimitExceededException.class)
    public ProblemDetail handlePlanLimit(PlanLimitExceededException ex) {
        return build(
                HttpStatus.PAYMENT_REQUIRED,
                ApiErrorCode.PLAN_LIMIT_EXCEEDED,
                ex.getMessage(),
                "/errors/plan-limit"
        );
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGeneral(Exception ex) {
        log.error("Beklenmeyen hata: {}", ex.getMessage(), ex);
        return build(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ApiErrorCode.INTERNAL_ERROR,
                "Beklenmeyen bir hata oluştu. Lütfen tekrar deneyin.",
                "/errors/internal"
        );
    }

    // ----------------------------------------------------------- //

    private static ProblemDetail build(HttpStatus status, ApiErrorCode code, String message, String typePath) {
        ProblemDetail problem = ProblemDetail.forStatus(status);
        problem.setTitle(code.name());
        problem.setDetail(message);
        problem.setType(URI.create(typePath));
        problem.setProperty("errorCode", code.name());
        // Geriye dönük uyumluluk için mevcut "message" alanını da koruyoruz.
        problem.setProperty("message", message);
        problem.setProperty("timestamp", Instant.now().toString());
        return problem;
    }
}
