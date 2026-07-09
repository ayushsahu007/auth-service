package com.abc.auth.repository;

import com.abc.auth.model.User;
import com.abc.auth.model.UserSession;
import com.abc.auth.model.enums.SessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserSessionRepository extends JpaRepository<UserSession, Long> {

    Optional<UserSession> findBySessionId(UUID sessionId);

    Optional<UserSession> findBySessionIdAndStatus(
            UUID sessionId,
            SessionStatus status
    );

    List<UserSession> findAllByUserAndStatus(
            User user,
            SessionStatus status
    );

    List<UserSession> findAllByUserOrderByLastUsedAtDesc(
            User user
    );

    List<UserSession> findAllByExpiresAtBefore(
            LocalDateTime dateTime
    );

    List<UserSession> findAllByAbsoluteExpiresAtBefore(
            LocalDateTime dateTime
    );

}