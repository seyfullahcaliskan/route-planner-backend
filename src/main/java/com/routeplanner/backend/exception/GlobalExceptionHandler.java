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
        problem.setType(URI.create("/errors/validation"));
        problem.setProperty("errors", errors);
        problem.setProperty("timestamp", Instant.now().toString());
        return problem;
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ProblemDetail handleResourceNotFound(ResourceNotFoundException ex) {
        log.warn("Resource not found: {}", ex.getMessage());
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        problem.setTitle("Kaynak bulunamadı");
        problem.setDetail(ex.getMessage());
        problem.setType(URI.create("/errors/not-found"));
        problem.setProperty("timestamp", Instant.now().toString());
        return problem;
    }

    @ExceptionHandler(OptimizationException.class)
    public ProblemDetail handleOptimization(OptimizationException ex) {
        log.error("Optimization error: {}", ex.getMessage(), ex);
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.UNPROCESSABLE_ENTITY);
        problem.setTitle("Optimizasyon hatası");
        problem.setDetail(ex.getMessage());
        problem.setType(URI.create("/errors/optimization"));
        problem.setProperty("timestamp", Instant.now().toString());
        return problem;
    }

    @ExceptionHandler(GeocodingException.class)
    public ProblemDetail handleGeocoding(GeocodingException ex) {
        log.warn("Geocoding error: {}", ex.getMessage());
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.UNPROCESSABLE_ENTITY);
        problem.setTitle("Adres çözümlenemedi");
        problem.setDetail(ex.getMessage());
        problem.setType(URI.create("/errors/geocoding"));
        problem.setProperty("timestamp", Instant.now().toString());
        return problem;
    }

    @ExceptionHandler(PlanLimitExceededException.class)
    public ProblemDetail handlePlanLimit(PlanLimitExceededException ex) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.PAYMENT_REQUIRED);
        problem.setTitle("Plan limiti aşıldı");
        problem.setDetail(ex.getMessage());
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
        problem.setType(URI.create("/errors/internal"));
        problem.setProperty("timestamp", Instant.now().toString());
        return problem;
    }
}