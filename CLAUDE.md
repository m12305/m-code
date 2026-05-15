# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Overview

m-code is a coding practice platform (刷题系统) built as a Spring Boot 3.2.5 + Spring Cloud microservices application. Java 17, Maven multi-module.

## Build & Run

```bash
# Build all modules (from root)
mvn clean install -DskipTests

# Build a single module
mvn clean install -pl m-code-user -am -DskipTests

# Run a specific service (module must have spring-boot-maven-plugin configured)
mvn spring-boot:run -pl m-code-user
```

There are no existing tests — `-DskipTests` is needed until tests are written.

## Architecture

### Module Map

| Module | Port | Purpose | Has DB | Has MQ |
|--------|------|---------|--------|--------|
| `m-code-parent` | — | Parent POM, dependency management | — | — |
| `m-code-common` | — | Shared code (entities, enums, Result, config, exception handler) | — | — |
| `m-code-gateway` | 8080 | API gateway, JWT auth, CORS, routes | — | — |
| `m-code-user` | 8081 | User registration/login, profile | Yes | — |
| `m-code-question` | — | Question/Category/Tag/Section CRUD | Yes | — |
| `m-code-judge` | — | Code submission & judging | Yes | RabbitMQ |
| `m-code-exam` | — | Exams, exam questions, exam records | Yes | RabbitMQ |
| `m-code-knowledge` | — | Articles, knowledge categories, learning paths | Yes | — |

### Request Flow

```
Client → Gateway (8080) → JwtAuthFilter (JWT validation) → Route by path prefix → Service
```

The gateway validates JWT and forwards user identity to downstream services via headers:
- `X-User-Id` — extracted from JWT claims
- `X-Username`
- `X-User-Role`

Downstream controllers read these via `@RequestHeader`. Whitelisted paths (no auth required): `/api/user/login`, `/api/user/register`.

### Service Registration & Config

All services register with **Nacos** (discovery + config). Bootstrap config is in each module's `src/main/resources/bootstrap.yml`. Default Nacos address: `localhost:8848`.

### Shared Library (`m-code-common`)

Every module depends on `m-code-common`. It provides:
- **`BaseEntity`** — all entities extend this (id via `ASSIGN_ID`, createTime/updateTime auto-fill, `@TableLogic` deleted field)
- **`Result<T>`** — unified API response wrapper with static factories (`Result.ok()`, `Result.fail()`)
- **`ResultCode`** — enum of HTTP-aligned status codes (200, 400, 401, 403, 404, 500)
- **`BusinessException`** — runtime exception with a code; caught by `GlobalExceptionHandler` which returns `Result<Void>`
- **`MyBatisMetaObjectHandler`** — auto-fills createTime, updateTime, deleted on insert/update
- **`MybatisPlusConfig`** — registers MySQL pagination interceptor
- **Enums**: `DifficultyEnum`, `JudgeStatusEnum`, `LanguageEnum`, `QuestionTypeEnum`

### Code Patterns

**Layered architecture** (same in every service module):
```
controller/  → @RestController, @RequestMapping, calls service, returns Result<T>
service/     → interface
service/impl/→ @Service, @RequiredArgsConstructor, calls mapper
mapper/      → MyBatis-Plus BaseMapper<T> interface (no XML needed for basic CRUD)
entity/      → @TableName, extends BaseEntity
dto/         → request/response DTOs (some modules have this, not all)
```

**Authentication**: Password hashing via `BCrypt.hashpw()` / `BCrypt.checkpw()` (Hutool). JWT issued by user-service with userId/username/role claims, 7-day expiry. Validated at gateway.

**Database**: MyBatis-Plus with logic deletion (deleted=1 means deleted), auto-fill timestamps, pagination via `Page<T>`. No XML mapper files — all queries use `LambdaQueryWrapper`.

**Async judging**: The judge module publishes submission IDs to a RabbitMQ topic exchange (`judge.exchange` → `judge.queue`). `JudgeConsumer` processes them. Currently a stub implementation that marks all submissions ACCEPTED.

### Configuration Files

Each service has two YAML files:
- `bootstrap.yml` — `spring.application.name` + Nacos discovery/config addresses
- `application.yml` — datasource, MyBatis-Plus settings, logging, server port, and for gateway: route definitions

### Key Dependencies

- Spring Cloud 2023.0.3 + Spring Cloud Alibaba 2023.0.1.0
- MyBatis-Plus 3.5.7 (ORM with pagination, logic delete, auto-fill)
- MySQL 8.0.33 + Druid 1.2.23 (connection pooling)
- RabbitMQ (via `spring-boot-starter-amqp`) — judge and exam modules
- JWT: `io.jsonwebtoken` (jjwt) 0.12.6
- Hutool 5.8.29 (utilities, BCrypt, BeanUtil)
- Lombok (all modules)
- OpenFeign + LoadBalancer (service-to-service calls)
