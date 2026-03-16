CREATE TABLE IF NOT EXISTS workflow (
    id UUID PRIMARY KEY,
    name TEXT,
    version INT,
    is_active BOOLEAN,
    input_schema TEXT,
    start_step_id UUID,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS step (
    id UUID PRIMARY KEY,
    workflow_id UUID,
    name TEXT,
    step_type TEXT,
    step_order INT,
    metadata TEXT,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS rule (
    id UUID PRIMARY KEY,
    step_id UUID,
    condition TEXT,
    next_step_id UUID,
    priority INT,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);