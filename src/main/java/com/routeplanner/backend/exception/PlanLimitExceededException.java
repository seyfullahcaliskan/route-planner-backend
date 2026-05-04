package com.routeplanner.backend.exception;

public class PlanLimitExceededException extends RuntimeException {
    public PlanLimitExceededException(int limit) {
        super(String.format(
                "Ücretsiz plan günlük %d durak sınırına ulaştı. Sınırsız kullanım için Pro'ya geçin.",
                limit
        ));
    }
}