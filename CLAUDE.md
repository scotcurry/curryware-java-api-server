# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Run Commands

```bash
./gradlew build          # Build the project
./gradlew test           # Run all tests
./gradlew bootRun        # Run the application (port 8080)
```

Java 17 toolchain is required (configured in build.gradle).

## Architecture

Spring Boot 4.0.0 REST API for a fantasy sports system backed by PostgreSQL. Follows a layered architecture: Controllers → Services → JDBC queries.

**Package structure** (`org.curryware`):
- `controllers/` — REST endpoints (`GameController`, `PlayerController`, `TransactionController`)
- `gameservice/`, `playerservice/`, `transactionservice/` — Business logic services and record DTOs
- `repositories/` — `PostgresConfig` (DataSource bean), `DatabaseConfigValidator` (env var validation), `PostgresRepository`

**REST Endpoints** (all GET):
- `/game_info/get_game_info` — Game/league info
- `/players/getplayers` — Player info
- `/transactions/get_transactions?gameId=X&leagueId=Y` — Transaction records (complex JOIN across multiple tables)

## Database

PostgreSQL connection configured via environment variables:
- `POSTGRES_DATABASE`, `POSTGRES_PASSWORD`, `POSTGRES_PORT`, `POSTGRES_SERVER`, `POSTGRES_USERNAME`

Services use `JdbcTemplate` directly for queries (no ORM). The `DatabaseConfigValidator` checks these env vars and tests network connectivity on startup.

## Testing

Tests use the `test` profile which disables `PostgresConfig` to avoid requiring a database connection. Currently only a context-load test exists. There is a known issue with Bean requirements in tests (see recent commits).

## CI/CD

GitHub Actions workflow (`.github/workflows/build-actions.yaml`) triggers on push to `master`:
1. Builds Docker image (multi-stage, Amazon Corretto 17)
2. Pushes to Docker Hub (`scotcurry4/curryware-java-api-server`)
3. Updates Kubernetes manifests in `scotcurry/k8s-manifests` repo

## Observability

- Logback with Logstash JSON encoder for structured logging
- Datadog static analysis configured (`static-analysis.datadog.yml`)
- Docker image includes Datadog environment variables (DD_VERSION, DD_GIT_REPOSITORY_URL, DD_GIT_COMMIT_SHA)
