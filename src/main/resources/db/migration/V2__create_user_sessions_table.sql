CREATE TABLE user_sessions
(
    id BIGSERIAL PRIMARY KEY,

    session_id UUID NOT NULL UNIQUE,

    user_id BIGINT NOT NULL,

    refresh_token_hash VARCHAR(64) NOT NULL,

    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',

    refresh_count INTEGER NOT NULL DEFAULT 0,

    device_name VARCHAR(100),

    browser VARCHAR(100),

    operating_system VARCHAR(100),

    ip_address VARCHAR(45),

    user_agent TEXT,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    last_used_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    expires_at TIMESTAMP NOT NULL,

    absolute_expires_at TIMESTAMP NOT NULL,

    revoked_at TIMESTAMP,

    version BIGINT NOT NULL DEFAULT 0,

    CONSTRAINT fk_user_sessions_user
        FOREIGN KEY (user_id)
            REFERENCES users(id)
            ON DELETE CASCADE,

    CONSTRAINT chk_user_session_status
        CHECK (status IN ('ACTIVE', 'REVOKED', 'EXPIRED'))
);

CREATE INDEX idx_user_sessions_user_id
    ON user_sessions(user_id);

CREATE INDEX idx_user_sessions_user_status
    ON user_sessions(user_id, status);

CREATE INDEX idx_user_sessions_expires_at
    ON user_sessions(expires_at);

CREATE INDEX idx_user_sessions_absolute_expires_at
    ON user_sessions(absolute_expires_at);