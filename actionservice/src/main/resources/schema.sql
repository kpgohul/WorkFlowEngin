CREATE TABLE IF NOT EXISTS execution_actions (
    id BIGSERIAL PRIMARY KEY,
    execution_id BIGINT NOT NULL,
    execution_step_id BIGINT NOT NULL,
    action_type VARCHAR(50) NOT NULL,
    is_active BOOLEAN,
    action_meta TEXT,
    initiated_at TIMESTAMP,
    completed_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS approval_requests (
    id BIGSERIAL PRIMARY KEY,
    execution_action_id BIGINT NOT NULL REFERENCES execution_actions(id) ON DELETE CASCADE,
    token VARCHAR(255) NOT NULL UNIQUE,
    is_approved BOOLEAN,
    approver_id BIGINT,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP,
    responded_at TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_execution_actions_execution_id ON execution_actions(execution_id);
CREATE INDEX IF NOT EXISTS idx_execution_actions_execution_step_id ON execution_actions(execution_step_id);
CREATE INDEX IF NOT EXISTS idx_approval_requests_execution_action_id ON approval_requests(execution_action_id);
