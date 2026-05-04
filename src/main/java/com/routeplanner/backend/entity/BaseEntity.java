package com.routeplanner.backend.entity;

import com.routeplanner.backend.enums.StatusEnum;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.UUID;

@Getter
@Setter
@MappedSuperclass
public abstract class BaseEntity {

    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id = UUID.randomUUID();

    @Column(name = "etag", nullable = false)
    private UUID etag = UUID.randomUUID();

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private StatusEnum status = StatusEnum.ACTIVE;

    @Column(name = "date_of_recorded", nullable = false)
    private Timestamp dateOfRecorded;

    @Column(name = "user_who_recorded", nullable = false, length = 50)
    private String userWhoRecorded = "SYSTEM";

    @Column(name = "date_of_last_updated", nullable = false)
    private Timestamp dateOfLastUpdated;

    @Column(name = "user_who_last_updated", nullable = false, length = 50)
    private String userWhoLastUpdated = "SYSTEM";

    @Column(name = "counter_of_unique_data", nullable = false)
    private Long counterOfUniqueData = 0L;

    @PrePersist
    protected void onCreate() {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        this.dateOfRecorded = now;
        this.dateOfLastUpdated = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.dateOfLastUpdated = new Timestamp(System.currentTimeMillis());
        this.etag = UUID.randomUUID();
        this.counterOfUniqueData++;
    }
}