CREATE TABLE usage_records (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    service_type VARCHAR(50) NOT NULL,
    usage_amount NUMERIC(12,2) NOT NULL,
    usage_unit VARCHAR(20) NOT NULL,
    recorded_at TIMESTAMP WITH TIME ZONE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_usage_records_user_id ON usage_records (user_id);
