package com.routeplanner.backend.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /** Bean validation (@Valid) hataları. */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = ex.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        fe -> fe.getDefaultMessage() != null ? fe.getDefaultMessage() : "Geçersiz değer",
                        (a, b) -> a
                ));

        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problem.setTitle("Geçersiz istek");
        // Mobile'ın kolay okuyabileceği 'message' alanına da yaz
        problem.setDetail(errors.values().stream().findFirst().orElse("Geçersiz istek"));
        problem.setProperty("message", errors.values().stream().findFirst().orElse("Geçersiz istek"));
        problem.setType(URI.create("/errors/validation"));
        problem.setProperty("errors", errors);
        problem.setProperty("timestamp", Instant.now().toString());
        return problem;
    }

    /**
     * İş mantığı hataları (kayıt/giriş validasyonları).
     * Örn: "Bu e-posta zaten kayıtlı", "E-posta veya şifre hatalı"
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleIllegalArgument(IllegalArgumentException ex) {
        log.debug("İş mantığı hatası: {}", ex.getMessage());
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        problem.setTitle("Geçersiz istek");
        problem.setDetail(ex.getMessage());
        // 'message' alanı — Axios interceptor'da e.response.data.message olarak okunur
        problem.setProperty("message", ex.getMessage());
        problem.setType(URI.create("/errors/business"));
        problem.setProperty("timestamp", Instant.now().toString());
        return problem;
    }

    /** OAuth ID token doğrulama hataları. */
    @ExceptionHandler(OAuthVerificationException.class)
    public ProblemDetail handleOAuth(OAuthVerificationException ex) {
        log.warn("OAuth doğrulama hatası: {}", ex.getMessage());
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);
        problem.setTitle("OAuth doğrulama başarısız");
        problem.setDetail(ex.getMessage());
        problem.setProperty("message", ex.getMessage());
        problem.setType(URI.create("/errors/oauth"));
        problem.setProperty("timestamp", Instant.now().toString());
        return problem;
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ProblemDetail handleResourceNotFound(ResourceNotFoundException ex) {
        log.warn("Kaynak bulunamadı: {}", ex.getMessage());
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        problem.setTitle("Kaynak bulunamadı");
        problem.setDetail(ex.getMessage());
        problem.setProperty("message", ex.getMessage());
        problem.setType(URI.create("/errors/not-found"));
        problem.setProperty("timestamp", Instant.now().toString());
        return problem;
    }

    @ExceptionHandler(OptimizationException.class)
    public ProblemDetail handleOptimization(OptimizationException ex) {
        log.error("Optimizasyon hatası: {}", ex.getMessage(), ex);
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.UNPROCESSABLE_ENTITY);
        problem.setTitle("Optimizasyon hatası");
        problem.setDetail(ex.getMessage());
        problem.setProperty("message", ex.getMessage());
        problem.setType(URI.create("/errors/optimization"));
        problem.setProperty("timestamp", Instant.now().toString());
        return problem;
    }

    @ExceptionHandler(GeocodingException.class)
    public ProblemDetail handleGeocoding(GeocodingException ex) {
        log.warn("Geocoding hatası: {}", ex.getMessage());
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.UNPROCESSABLE_ENTITY);
        problem.setTitle("Adres çözümlenemedi");
        problem.setDetail(ex.getMessage());
        problem.setProperty("message", ex.getMessage());
        problem.setType(URI.create("/errors/geocoding"));
        problem.setProperty("timestamp", Instant.now().toString());
        return problem;
    }

    @ExceptionHandler(PlanLimitExceededException.class)
    public ProblemDetail handlePlanLimit(PlanLimitExceededException ex) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.PAYMENT_REQUIRED);
        problem.setTitle("Plan limiti aşıldı");
        problem.setDetail(ex.getMessage());
        problem.setProperty("message", ex.getMessage());
        problem.setType(URI.create("/errors/plan-limit"));
        problem.setProperty("timestamp", Instant.now().toString());
        return problem;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGeneral(Exception ex) {
        log.error("Beklenmeyen hata: {}", ex.getMessage(), ex);
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        problem.setTitle("Sunucu hatası");
        problem.setDetail("Beklenmeyen bir hata oluştu. Lütfen tekrar deneyin.");
        problem.setProperty("message", "Beklenmeyen bir hata oluştu.");
        problem.setType(URI.create("/errors/internal"));
        problem.setProperty("timestamp", Instant.now().toString());
        return problem;
    }
}