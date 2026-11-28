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
- `controllers/` — REST endpoints (`GameController`, `PlayerController`, `TeamController`, `TransactionController`, `NotificationTokenHandlerController`)
- `gameservice/`, `playerservice/`, `teamservice/`, `transactionservice/`, `userservice/` — Business logic services plus data-holding classes (e.g. `TransactionRecord`)
- `repositories/` — `PostgresConfig` (DataSource/JdbcTemplate beans, active on `!test` profile), `DatabaseConfigValidator` (env var read + TCP connectivity check on startup), `PostgresRepository` (legacy query methods, largely superseded by service-layer queries)

**REST Endpoints** — every controller is mapped under `/v1`. Through the cluster ingress they are
reached at `https://ubuntu-microk8s.curryware.org/java/v1/...` (Traefik strips the `/java` prefix).
The controllers' `@RequestMapping`/`@GetMapping` annotations are the source of truth:
- `GET /v1/game_info/get_game_info` — Game/league info
- `GET /v1/team_info/get_team_info?gameId=X&leagueId=Y` — Team info for a league
- `GET /v1/team_info/get_teams/{leagueKey}` — Teams in a league
- `GET /v1/transactions/get_transactions?leagueId=X&transactionTime=Y` — Waiver transactions (`transaction_info` JOIN `transaction_player` JOIN `player_info`)
- `GET /v1/player_info/get_best_available?gameId=X&leagueId=Y[&weeks=N][&position=QB|RB|WR|TE][&minGames=N][&sort=total|per_game][&limit=N]` — A league's available players ranked by fantasy points under the league's scoring (see Best Available Players)
- `POST /v1/notificationhandler/storeToken` — Stores a device notification token (JSON body `DeviceTokenRequest`)

Unknown paths return 500, not 404: `GlobalExceptionHandler` also catches Spring's `NoResourceFoundException`.
Missing or non-numeric parameters and `IllegalArgumentException` return 400.

## Best Available Players (`playerservice/`)
- A league's available players are read from `yahoo_available_players`, which the `curryware-nflverse`
  available-players CronJobs (one per league, 838859 and 523652) replace every 6 hours. The service never calls
  the Cloud Function itself. The response's `availableAsOf` is the league's `max(loaded_at)`, so the list can be
  up to about 6 hours old. A league without a CronJob has no rows and gets a 404.
- `BestAvailableService` does the arithmetic in Postgres: available Yahoo ids → `nflverse_players.yahoo_id`
  (for the league's season from `all_league_information`) → `nflverse_player_week_stats`, × the league's
  `league_scoring_value_information` values, regular-season weeks only. `weeks=N` counts back from the
  latest week that has any stats, including a week still in progress; omitted, it is the season to date.
- `YahooStatMapping` maps Yahoo stat ids to nflverse columns (16 rows, offensive stats only). Scoring every
  2026 row with standard PPR values through it matched nflverse's `fantasy_points_ppr` exactly. Kicking and
  defense stats can't be scored (nflverse_players has only QB/RB/WR/TE); the response lists the league's
  unscored stat ids in `unscoredStatIds`.
- 404 when the league, its scoring rules, the season's stats or its available-players list aren't loaded.

## Database

PostgreSQL connection configured via environment variables:
- `POSTGRES_DATABASE`, `POSTGRES_PASSWORD`, `POSTGRES_PORT`, `POSTGRES_SERVER`, `POSTGRES_USERNAME`

Services use `JdbcTemplate` directly (no ORM). `PostgresConfig` reads env vars via `DatabaseConfigValidator`, does a TCP socket check (3 s timeout) before building the `DataSource`, and logs an error (but does not abort) if the DB is unreachable. HikariCP is configured with `initialization-fail-timeout=-1` so the app starts even when the database is down.

**Query pattern**: `PostgresRepository` holds simple generic queries returning `List<Map<String, Object>>`. More complex queries with typed results use an inline `RowMapper` defined in the service class (see `TransactionService`). New queries should follow the service-layer pattern.

**Data classes**: `TransactionRecord` (and similar) are mutable JavaBeans with explicit getters/setters, not Java records.

## Testing

Tests run under the `test` Spring profile. `TestDatabaseConfig` (`@Profile("test")`) supplies Mockito-mocked `DataSource` and `JdbcTemplate` beans, preventing `PostgresConfig` from running and removing the need for a real database connection.

- Controllers: standalone `MockMvc` with `GlobalExceptionHandler` and a mocked service (see `PlayerControllerTest`).
- SQL: `BestAvailableServicePostgresTest` runs against a throwaway Postgres named by
  `CURRYWARE_TEST_POSTGRES_URL` (plus `_USER`/`_PASSWORD`), building tables from the
  `../curryware-fantasy-database` `create_*.sql` files (override with `CURRYWARE_SCHEMA_DIR`), so schema
  drift breaks it. Each test uses its own schema, and it refuses to run against `currywarefantasy`. Without
  the variable it is skipped. Setup steps are in the class comment.

## CI/CD

`.github/workflows/ci.yml` runs `./gradlew test` on every branch push and pull request, with a
`postgres:17` service container and the private `scotcurry/curryware-fantasy-database` checked out for the
DDL (secret `CURRYWARE_FANTASY_DATABASE_TOKEN`: a fine-grained token with read-only Contents access to that
repo), so the SQL tests really run. A final step fails the run if any `*Postgres*` test was skipped. It builds nothing
deployable.

`.github/workflows/build-actions.yaml` triggers on push to `master`. One deploy runs at a time:
1. Calls `ci.yml` (job `test`); nothing below runs unless it passes
2. Builds Docker image (multi-stage, Amazon Corretto 17; its `./gradlew build` runs the tests again, minus
   the SQL tests)
3. Pushes to Docker Hub (`scotcurry4/curryware-java-api-server`)
4. Updates Kubernetes manifests in `scotcurry/k8s-manifests` repo

## Observability

- Logback with Logstash JSON encoder (`logback-spring.xml`) for structured JSON logging
- Log level for `org.curryware` set to `DEBUG` in `application.properties`
- Datadog static analysis configured (`static-analysis.datadog.yml`)
- Docker image includes Datadog environment variables (`DD_VERSION`, `DD_GIT_REPOSITORY_URL`, `DD_GIT_COMMIT_SHA`)
