CREATE EXTENSION IF NOT EXISTS pgcrypto;
CREATE TABLE notifications (
    id uuid PRIMARY KEY,
    idempotency_key varchar(200) NOT NULL UNIQUE,
    user_id varchar(200) NOT NULL,
    channel varchar(20) NOT NULL,
    priority varchar(20) NOT NULL,
    status varchar(20) NOT NULL,
    recipient varchar(500) NOT NULL,
    template_code varchar(100),
    body text,
    template_params jsonb,
    metadata jsonb,
    retry_count integer NOT NULL DEFAULT 0,
    last_error text,
    created_at timestamptz NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamptz NULL,
    next_attempt_at timestamptz
);
CREATE INDEX ix_notifications_user_created ON notifications(user_id, created_at DESC);
CREATE INDEX ix_notifications_status ON notifications(status);
CREATE TABLE delivery_audit (
    id uuid PRIMARY KEY,
    notification_id uuid NOT NULL REFERENCES notifications(id) ON DELETE CASCADE,
    from_status varchar(20),
    to_status varchar(20) NOT NULL,
    detail text,
    created_at timestamptz NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX ix_delivery_audit_notification ON delivery_audit(notification_id, created_at);
CREATE TABLE notification_templates (
    id uuid PRIMARY KEY,
    code varchar(100) NOT NULL UNIQUE,
    channel varchar(20) NOT NULL,
    subject varchar(200) NOT NULL,
    body text NOT NULL,
    active boolean NOT NULL DEFAULT true,
    created_at timestamptz NOT NULL DEFAULT CURRENT_TIMESTAMP
);
INSERT INTO notification_templates(id,code,channel,subject,body) VALUES
(gen_random_uuid(),'WELCOME_EMAIL','EMAIL','Welcome','Hello {{userName}}, welcome to {{appName}}.'),
(gen_random_uuid(),'OTP_SMS','SMS','OTP','Your OTP is {{otp}}.'),
(gen_random_uuid(),'ORDER_CONFIRMATION_EMAIL','EMAIL','Order confirmation','Order {{orderId}} has been confirmed.'),
(gen_random_uuid(),'PAYMENT_SUCCESS_PUSH','PUSH','Payment successful','Payment {{paymentId}} was successful.'),
(gen_random_uuid(),'PASSWORD_RESET_EMAIL','EMAIL','Password reset','Your password reset code is {{code}}.'),
(gen_random_uuid(),'LOW_BALANCE_SMS','SMS','Low balance','Your balance is {{balance}}.'),
(gen_random_uuid(),'PROMO_PUSH','PUSH','Promotion','{{message}}');
