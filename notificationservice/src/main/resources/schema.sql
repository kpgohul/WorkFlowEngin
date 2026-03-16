CREATE TABLE notification_logs (
    id UUID PRIMARY KEY,
    execution_id UUID,
    event_type TEXT,
    recipient TEXT,
    message TEXT,
    status TEXT,
    created_at TIMESTAMP
);