CREATE TABLE users (
    id VARCHAR(36) PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(100) NOT NULL,
    created_at BIGINT NOT NULL,
    updated_at BIGINT NOT NULL
);

CREATE TABLE categories (
    id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL,
    name VARCHAR(120) NOT NULL,
    icon VARCHAR(50),
    color VARCHAR(50),
    pinned BOOLEAN NOT NULL DEFAULT FALSE,
    pinned_order INTEGER NOT NULL DEFAULT -1,
    sort_order INTEGER NOT NULL DEFAULT 0,
    hidden BOOLEAN NOT NULL DEFAULT FALSE,
    created_at BIGINT NOT NULL,
    updated_at BIGINT NOT NULL,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at BIGINT,
    version BIGINT NOT NULL DEFAULT 1,
    last_modified_device_id VARCHAR(80),
    CONSTRAINT fk_categories_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE tasks (
    id VARCHAR(36) PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    category_id VARCHAR(36),
    deadline VARCHAR(50),
    completed BOOLEAN NOT NULL DEFAULT FALSE,
    wont_do BOOLEAN NOT NULL DEFAULT FALSE,
    priority VARCHAR(50),
    image_path VARCHAR(500),
    updated_at BIGINT NOT NULL,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at BIGINT,
    user_id VARCHAR(36),
    version BIGINT NOT NULL DEFAULT 1,
    last_modified_device_id VARCHAR(80),
    CONSTRAINT fk_tasks_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_tasks_category FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE SET NULL
);

CREATE TABLE habits (
    id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL,
    title VARCHAR(255) NOT NULL,
    group_name VARCHAR(120),
    frequency VARCHAR(20) NOT NULL DEFAULT 'daily',
    total_days INTEGER NOT NULL DEFAULT 0,
    color INTEGER NOT NULL DEFAULT 0,
    sort_order INTEGER NOT NULL DEFAULT 0,
    created_at BIGINT NOT NULL,
    updated_at BIGINT NOT NULL,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at BIGINT,
    version BIGINT NOT NULL DEFAULT 1,
    last_modified_device_id VARCHAR(80),
    CONSTRAINT fk_habits_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE habit_completions (
    id VARCHAR(80) PRIMARY KEY,
    habit_id VARCHAR(36) NOT NULL,
    user_id VARCHAR(36) NOT NULL,
    period_key VARCHAR(32) NOT NULL,
    completed_at BIGINT NOT NULL,
    updated_at BIGINT NOT NULL,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at BIGINT,
    version BIGINT NOT NULL DEFAULT 1,
    last_modified_device_id VARCHAR(80),
    CONSTRAINT fk_habit_completions_habit FOREIGN KEY (habit_id) REFERENCES habits(id) ON DELETE CASCADE,
    CONSTRAINT fk_habit_completions_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT uq_habit_completions_period UNIQUE (user_id, habit_id, period_key)
);

CREATE TABLE user_sessions (
    id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL,
    device_id VARCHAR(80) NOT NULL,
    refresh_token_hash VARCHAR(64) NOT NULL UNIQUE,
    created_at BIGINT NOT NULL,
    updated_at BIGINT NOT NULL,
    expires_at BIGINT NOT NULL,
    revoked_at BIGINT,
    CONSTRAINT fk_user_sessions_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE sync_operations (
    id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL,
    device_id VARCHAR(80) NOT NULL,
    entity_type VARCHAR(40) NOT NULL,
    entity_id VARCHAR(80) NOT NULL,
    operation VARCHAR(20) NOT NULL,
    base_version BIGINT NOT NULL DEFAULT 0,
    client_timestamp BIGINT NOT NULL,
    server_timestamp BIGINT NOT NULL,
    payload_json TEXT,
    CONSTRAINT ck_sync_operations_entity_type CHECK (
        entity_type IN ('TASK', 'CATEGORY', 'HABIT', 'HABIT_COMPLETION')
    ),
    CONSTRAINT ck_sync_operations_operation CHECK (operation IN ('CREATE', 'UPDATE', 'DELETE', 'RESTORE')),
    CONSTRAINT fk_sync_operations_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_categories_user_visible ON categories(user_id, deleted, hidden, pinned_order, sort_order);
CREATE INDEX idx_tasks_user_active ON tasks(user_id, deleted, updated_at);
CREATE INDEX idx_tasks_category ON tasks(category_id);
CREATE INDEX idx_habits_user_active ON habits(user_id, deleted, sort_order);
CREATE INDEX idx_habit_completions_user_active ON habit_completions(user_id, deleted, updated_at);
CREATE INDEX idx_user_sessions_user_active ON user_sessions(user_id, revoked_at, expires_at);
CREATE INDEX idx_sync_operations_user_since ON sync_operations(user_id, server_timestamp);
CREATE INDEX idx_sync_operations_entity ON sync_operations(user_id, entity_type, entity_id);
