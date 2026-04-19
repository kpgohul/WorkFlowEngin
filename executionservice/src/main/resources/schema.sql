--DROP SCHEMA public CASCADE;
--CREATE SCHEMA public;

CREATE TABLE IF NOT EXISTS workflow_executions (
    id BIGSERIAL PRIMARY KEY,
    workflow_id BIGINT NOT NULL,
    workflow_type_id BIGINT NOT NULL,
    current_step_id BIGINT,
    error TEXT,
    input_payload TEXT,
    status VARCHAR(50),
    initiated_by BIGINT,
    initiated_at TIMESTAMP,
    terminated_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS workflow_execution_steps (
    id BIGSERIAL PRIMARY KEY,
    workflow_execution_id BIGINT NOT NULL,
    step_id BIGINT NOT NULL,
    step_name VARCHAR(255),
    steptype VARCHAR(50),
    order_of_execution INTEGER,
    status VARCHAR(50),
    message TEXT,
    error TEXT,
    initiated_at TIMESTAMP,
    terminated_at TIMESTAMP,

    CONSTRAINT fk_workflow_execution
        FOREIGN KEY (workflow_execution_id)
        REFERENCES workflow_executions(id)
        ON DELETE CASCADE
);