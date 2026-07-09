package com.abc.auth.model;

import com.abc.auth.model.enums.SessionStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "user_sessions",
        indexes = {
                @Index(name = "idx_user_sessions_user_id", columnList = "user_id"),
                @Index(name = "idx_user_sessions_user_status", columnList = "user_id,status"),
                @Index(name = "idx_user_sessions_expires_at", columnList = "expires_at"),
                @Index(name = "idx_user_sessions_absolute_expires_at", columnList = "absolute_expires_at")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "session_id", nullable = false, unique = true)
    private UUID sessionId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "user_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_user_sessions_user")
    )
    private User user;

    @Column(name = "refresh_token_hash", nullable = false, length = 64)
    private String refreshTokenHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SessionStatus status;

    @Column(name = "refresh_count", nullable = false)
    @Builder.Default
    private Integer refreshCount = 0;

    @Column(name = "device_name", length = 100)
    private String deviceName;

    @Column(length = 100)
    private String browser;

    @Column(name = "operating_system", length = 100)
    private String operatingSystem;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "last_used_at", nullable = false)
    private LocalDateTime lastUsedAt;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "absolute_expires_at", nullable = false)
    private LocalDateTime absoluteExpiresAt;

    @Column(name = "revoked_at")
    private LocalDateTime revokedAt;

    @Version
    @Column(nullable = false)
    private Long version;

    @PrePersist
    public void prePersist() {

        LocalDateTime now = LocalDateTime.now();

        this.createdAt = now;
        this.updatedAt = now;
        this.lastUsedAt = now;

        if (this.sessionId == null) {
            this.sessionId = UUID.randomUUID();
        }

        if (this.status == null) {
            this.status = SessionStatus.ACTIVE;
        }

        if (this.refreshCount == null) {
            this.refreshCount = 0;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

}