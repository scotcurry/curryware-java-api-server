# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Run Commands

```bash
./gradlew build                                          # Build the project
./gradlew test                                           # Run all tests
./gradlew test --tests "ClassName.methodName"            # Run a single test
./gradlew bootRun                                        # Run the application (port 8080)
```

Java 17 toolchain is required (configured in build.gradle). Spring Boot 4.0.6.

## Architecture

Spring Boot REST API for a fantasy sports system backed by PostgreSQL. Follows a layered architecture: Controllers → Services → JDBC queries.

**Package structure** (`org.curryware`):
- `controllers/` — REST endpoints (`GameController`, `PlayerController`, `TransactionController`)
- `gameservice/`, `playerservice/`, `transactionservice/` — Business logic services plus data-holding classes (e.g. `TransactionRecord`)
- `repositories/` — `PostgresConfig` (DataSource/JdbcTemplate beans, active on `!test` profile), `DatabaseConfigValidator` (env var read + TCP connectivity check on startup), `PostgresRepository` (legacy query methods, largely superseded by service-layer queries)

**REST Endpoints** (all GET):
- `/game_info/get_game_info` — Game/league info
- `/players/getplayers` — Player info
- `/transactions/get_transactions?gameId=X&leagueId=Y` — Waiver transactions (JOIN across `transaction_player`, `player_info`, `team_info`, `transaction_info`)

## Database

PostgreSQL connection configured via environment variables:
- `POSTGRES_DATABASE`, `POSTGRES_PASSWORD`, `POSTGRES_PORT`, `POSTGRES_SERVER`, `POSTGRES_USERNAME`

Services use `JdbcTemplate` directly (no ORM). `PostgresConfig` reads env vars via `DatabaseConfigValidator`, does a TCP socket check (3 s timeout) before building the `DataSource`, and logs an error (but does not abort) if the DB is unreachable. HikariCP is configured with `initialization-fail-timeout=-1` so the app starts even when the database is down.

**Query pattern**: `PostgresRepository` holds simple generic queries returning `List<Map<String, Object>>`. More complex queries with typed results use an inline `RowMapper` defined in the service class (see `TransactionService`). New queries should follow the service-layer pattern.

**Data classes**: `TransactionRecord` (and similar) are mutable JavaBeans with explicit getters/setters, not Java records.

## Testing

Tests run under the `test` Spring profile. `TestDatabaseConfig` (`@Profile("test")`) supplies Mockito-mocked `DataSource` and `JdbcTemplate` beans, preventing `PostgresConfig` from running and removing the need for a real database connection. Currently only a context-load test exists.

## CI/CD

GitHub Actions workflow (`.github/workflows/build-actions.yaml`) triggers on push to `master`:
1. Builds Docker image (multi-stage, Amazon Corretto 17)
2. Pushes to Docker Hub (`scotcurry4/curryware-java-api-server`)
3. Updates Kubernetes manifests in `scotcurry/k8s-manifests` repo

## Observability

- Logback with Logstash JSON encoder (`logback-spring.xml`) for structured JSON logging
- Log level for `org.curryware` set to `DEBUG` in `application.properties`
- Datadog static analysis configured (`static-analysis.datadog.yml`)
- Docker image includes Datadog environment variables (`DD_VERSION`, `DD_GIT_REPOSITORY_URL`, `DD_GIT_COMMIT_SHA`)
