# High-Throughput Notification Engine

A production-grade notification system built with Java 25, Spring Boot 4, Apache Kafka, PostgreSQL, and Redis. Designed to reliably deliver thousands notifications per hour across Email, SMS, and Push channels.

## Tech Stack

- Java 25
- Spring Boot 4.x
- PostgreSQL
- Apache Kafka
- Redis 7.x
- Flyway
- Micrometer + Prometheus
- Spring Mail
- Docker Compose

## Architecture

The project is organized by **vertical slice**, rather than global controller/service/repository layers:

```text
com.sinobash.api.notification_engine
├── notification
│   ├── api
│   ├── application
│   ├── domain
│   └── infrastructure
├── template
│   ├── api
│   ├── application
│   ├── domain
│   └── infrastructure
├── admin
│   └── api
└── shared
    ├── config
    └── error
```

The notification slice owns its API, domain model, orchestration, Kafka publishing/consuming, Redis idempotency, retries and channel-dispatch boundary. Template and admin functionality remain separate slices.

## Core flow

```text
Client
  │ POST /api/v1/notifications
  ▼
Notification API
  │
  ├── PostgreSQL: notification + audit
  └── Kafka
       ├── notifications.high
       └── notifications.low
                 │
                 ▼
           Notification worker
                 │
                 ├── Redis idempotency lock
                 ├── QUEUED → DISPATCHED
                 ├── template rendering
                 ├── channel adapter
                 └── DISPATCHED → DELIVERED
                         │
                       failure
                         ▼
                 exponential retry state
                         │
                         └── retry scheduler → Kafka
                                      │
                               max attempts
                                      ▼
                              notifications.dlq
```

## Quick start

Requirements: Docker Desktop/Engine.

```bash
docker compose up --build -d
curl http://localhost:8080/api/v1/admin/health
```

Send a notification:

```bash
curl -X POST http://localhost:8080/api/v1/notifications \
  -H 'Content-Type: application/json' \
  -d '{
    "idempotencyKey": "welcome-user-42-001",
    "userId": "user-42",
    "channel": "EMAIL",
    "priority": "HIGH",
    "templateCode": "WELCOME_EMAIL",
    "templateParams": {"userName":"Alex","appName":"DemoApp"},
    "recipient": "alex@example.com"
  }'
```

By default email is a **log adapter**, so the smoke test does not require an SMTP server. Set `MAIL_ENABLED=true` and configure `MAIL_HOST` / `MAIL_PORT` to enable SMTP delivery.

Check status:

```bash
curl http://localhost:8080/api/v1/notifications/key/welcome-user-42-001
```

Prometheus endpoint:

```text
http://localhost:8080/actuator/prometheus
```

## API

- `POST /api/v1/notifications`
- `POST /api/v1/notifications/batch`
- `GET /api/v1/notifications/{id}`
- `GET /api/v1/notifications/key/{idempotencyKey}`
- `GET /api/v1/notifications/user/{userId}?page=0&size=20`
- `GET /api/v1/notifications/stats`
- `GET /api/v1/templates`
- `POST /api/v1/templates`
- `GET /api/v1/admin/audit/{notificationId}`
- `GET /api/v1/admin/health`
- `GET /actuator/prometheus`

## Delivery adapters

Email is implemented through Spring Mail when enabled. SMS and Push are logging adapters for MVP. All channels are behind `ChannelDispatcher`, so Twilio, AWS SNS, FCM, APNs, etc. can be added without changing the notification domain or public API.