--DROP SCHEMA public CASCADE;
--CREATE SCHEMA public;


CREATE TABLE IF NOT EXISTS execution_actions (
    id BIGSERIAL PRIMARY KEY,
    execution_id BIGINT NOT NULL,
    execution_step_id BIGINT,
    action_type VARCHAR(50),
    is_active BOOLEAN,
    action_meta TEXT,
    initiated_at TIMESTAMP,
    completed_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS approval_requests (
    id BIGSERIAL PRIMARY KEY,
    execution_action_id BIGINT NOT NULL,
    token VARCHAR(255),
    is_approved BOOLEAN,
    approver_id BIGINT,
    status VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    responded_at TIMESTAMP,

    CONSTRAINT fk_execution_action
        FOREIGN KEY (execution_action_id)
        REFERENCES execution_actions(id)
        ON DELETE CASCADE
);