package org.curryware.playerservice;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * Runs the best-available SQL against a real, throwaway Postgres, with tables created from the
 * curryware-fantasy-database create_*.sql files (the schema source of truth), so schema drift
 * breaks this test. Skipped unless CURRYWARE_TEST_POSTGRES_URL is set. Each test uses its own
 * schema, dropped afterwards, and the test refuses to run against currywarefantasy. To run:
 *
 *   podman run -d --rm --name java-api-pytest-pg -p 55434:5432 -e POSTGRES_USER=test \
 *       -e POSTGRES_PASSWORD=test -e POSTGRES_DB=curryware_test docker.io/library/postgres:17
 *   CURRYWARE_TEST_POSTGRES_URL=jdbc:postgresql://localhost:55434/curryware_test \
 *       CURRYWARE_TEST_POSTGRES_USER=test CURRYWARE_TEST_POSTGRES_PASSWORD=test ./gradlew test
 *   podman stop java-api-pytest-pg
 *
 * The DDL is read from ../curryware-fantasy-database (override with CURRYWARE_SCHEMA_DIR).
 */
class BestAvailableServicePostgresTest {

    private static final String PRODUCTION_DATABASE = "currywarefantasy";
    private static final List<String> DDL_FILES = List.of(
            "create_all_league_information.sql",
            "create_league_scoring_value_information.sql",
            "create_nflverse_players_table.sql",
            "create_nflverse_player_week_stats_table.sql",
            "create_yahoo_available_players_table.sql");

    private static final int GAME = 470;
    private static final int LEAGUE = 523652;
    private static final int SEASON = 2026;
    private static final Instant LOADED_AT = Instant.parse("2026-11-27T12:00:00Z");

    // Available: QB 101, RB 102, WR 103, TE 105 (no stats) and 999 (a kicker, not in nflverse).
    // WR 104 has the best week but is rostered, so it must never appear.
    private static final List<Integer> AVAILABLE = List.of(101, 102, 103, 105, 999);

    private String adminUrl;
    private String user;
    private String password;
    private String schema;
    private JdbcTemplate jdbc;
    private BestAvailableService service;

    @BeforeEach
    void setUp() throws IOException {
        adminUrl = System.getenv("CURRYWARE_TEST_POSTGRES_URL");
        assumeTrue(adminUrl != null && !adminUrl.isBlank(),
                "set CURRYWARE_TEST_POSTGRES_URL to run tests against a throwaway Postgres");
        if (adminUrl.contains("/" + PRODUCTION_DATABASE)) {
            fail("CURRYWARE_TEST_POSTGRES_URL must not name the " + PRODUCTION_DATABASE + " database");
        }
        Path schemaDir = Path.of(System.getenv().getOrDefault("CURRYWARE_SCHEMA_DIR", "../curryware-fantasy-database"));
        assumeTrue(Files.isDirectory(schemaDir), "schema DDL directory " + schemaDir + " not found");
        user = System.getenv("CURRYWARE_TEST_POSTGRES_USER");
        password = System.getenv("CURRYWARE_TEST_POSTGRES_PASSWORD");

        schema = "junit_" + UUID.randomUUID().toString().replace("-", "").substring(0, 12);
        admin().execute("CREATE SCHEMA " + schema);
        String separator = adminUrl.contains("?") ? "&" : "?";
        jdbc = new JdbcTemplate(new DriverManagerDataSource(adminUrl + separator + "currentSchema=" + schema, user, password));
        for (String file : DDL_FILES) {
            jdbc.execute(Files.readString(schemaDir.resolve(file)));
        }
        loadFixtures();

        service = new BestAvailableService(jdbc);
    }

    @AfterEach
    void dropSchema() {
        if (schema != null && adminUrl != null) {
            admin().execute("DROP SCHEMA IF EXISTS " + schema + " CASCADE");
        }
    }

    private JdbcTemplate admin() {
        return new JdbcTemplate(new DriverManagerDataSource(adminUrl, user, password));
    }

    private void loadFixtures() {
        jdbc.update("INSERT INTO all_league_information (league_key, league_id, game_id, league_name, number_of_teams, season) " +
                "VALUES ('470.l.523652', ?, ?, 'Test League', 12, ?), ('470.l.1', 1, ?, 'No Scoring League', 10, ?)",
                LEAGUE, GAME, SEASON, GAME, SEASON);
        // Standard PPR values, plus a kicker stat (19) we can't score and a zero-value stat (54).
        Object[][] scoring = {
                {4, "0.04"}, {5, "4"}, {6, "-2"}, {9, "0.1"}, {10, "6"}, {11, "1"}, {12, "0.1"},
                {13, "6"}, {16, "2"}, {18, "-2"}, {19, "3"}, {54, "0"}};
        for (Object[] rule : scoring) {
            jdbc.update("INSERT INTO league_scoring_value_information VALUES (?, ?, ?, ?)",
                    GAME, LEAGUE, rule[0], new BigDecimal((String) rule[1]));
        }
        Object[][] players = {
                {"00-0001", 101, "Test Quarterback", "QB", "NE"},
                {"00-0002", 102, "Test Runningback", "RB", "BAL"},
                {"00-0003", 103, "Test Receiver", "WR", "CIN"},
                {"00-0004", 104, "Rostered Receiver", "WR", "DAL"},
                {"00-0005", 105, "Test Tight End", "TE", "KC"}};
        for (Object[] p : players) {
            jdbc.update("INSERT INTO nflverse_players (season, gsis_id, yahoo_id, full_name, position, team) " +
                    "VALUES (?, ?, ?, ?, ?, ?)", SEASON, p[0], p[1], p[2], p[3], p[4]);
        }
        // QB: 18, 14 and 8 points in weeks 1-3 (40 total).
        stat("00-0001", 1, "passing_yards", 300, "passing_tds", 2, "passing_interceptions", 1);
        stat("00-0001", 2, "passing_yards", 250, "passing_tds", 1);
        stat("00-0001", 3, "passing_yards", 200);
        // RB: 20 in week 1, a bye in week 2, 3 in week 3 (23 total, 2 games).
        stat("00-0002", 1, "rushing_yards", 100, "rushing_tds", 1, "receptions", 2, "receiving_yards", 20);
        stat("00-0002", 3, "rushing_yards", 50, "rushing_fumbles_lost", 1);
        // WR: one 34-point game in week 3 (8 + 12 + 12 + 2-point conversion 2).
        stat("00-0003", 3, "receptions", 8, "receiving_yards", 120, "receiving_tds", 2,
                "receiving_2pt_conversions", 1);
        // Rostered WR: 50 points, but not available.
        stat("00-0004", 1, "receiving_yards", 200, "receiving_tds", 5);
        for (int yahooId : AVAILABLE) {
            jdbc.update("INSERT INTO yahoo_available_players (game_id, league_id, player_id, full_name, loaded_at) " +
                    "VALUES (?, ?, ?, ?, ?)", GAME, LEAGUE, yahooId, "Player " + yahooId, Timestamp.from(LOADED_AT));
        }
        // Another league's list, which must not leak into this league's results: 104 is free there.
        jdbc.update("INSERT INTO yahoo_available_players (game_id, league_id, player_id) VALUES (?, 838859, 104)", GAME);
        // A playoff game, which the regular-season windows must ignore.
        jdbc.update("INSERT INTO nflverse_player_week_stats (player_id, season, week, season_type, passing_yards) " +
                "VALUES ('00-0001', ?, 19, 'POST', 400)", SEASON);
    }

    private void stat(String playerId, int week, Object... columnsAndValues) {
        List<String> columns = new ArrayList<>(List.of("player_id", "season", "week", "season_type"));
        List<Object> values = new ArrayList<>(List.of(playerId, SEASON, week, "REG"));
        for (int i = 0; i < columnsAndValues.length; i += 2) {
            columns.add((String) columnsAndValues[i]);
            values.add(columnsAndValues[i + 1]);
        }
        String placeholders = String.join(", ", java.util.Collections.nCopies(values.size(), "?"));
        jdbc.update("INSERT INTO nflverse_player_week_stats (" + String.join(", ", columns) + ") VALUES (" +
                placeholders + ")", values.toArray());
    }

    private static List<Integer> ids(BestAvailableResponse response) {
        return response.getPlayers().stream().map(BestAvailablePlayer::getYahooPlayerId).toList();
    }

    @Test
    void seasonToDateRanksOnlyAvailablePlayersByTotalPoints() {
        BestAvailableResponse response = service.getBestAvailable(GAME, LEAGUE, null, null, 1, "total", 50);

        assertEquals(List.of(101, 103, 102), ids(response));
        BestAvailablePlayer qb = response.getPlayers().get(0);
        assertEquals("Test Quarterback", qb.getFullName());
        assertEquals("QB", qb.getPosition());
        assertEquals("NE", qb.getTeam());
        assertEquals(new BigDecimal("40.00"), qb.getTotalPoints());
        assertEquals(3, qb.getGamesPlayed());
        assertEquals(new BigDecimal("13.33"), qb.getPointsPerGame());
    }

    @Test
    void reportsTheWindowAvailabilityAndScoringCoverage() {
        BestAvailableResponse response = service.getBestAvailable(GAME, LEAGUE, null, null, 1, "total", 50);

        assertEquals(SEASON, response.getSeason());
        assertEquals(1, response.getFromWeek());
        assertEquals(3, response.getToWeek());
        assertEquals(LOADED_AT, response.getAvailableAsOf());
        assertEquals(5, response.getAvailablePlayerCount());
        assertEquals(4, response.getMatchedPlayerCount());
        assertEquals(List.of(19), response.getUnscoredStatIds());
    }

    @Test
    void byeWeeksCountAsNoGame() {
        BestAvailableResponse response = service.getBestAvailable(GAME, LEAGUE, null, "RB", 1, "total", 50);

        BestAvailablePlayer rb = response.getPlayers().get(0);
        assertEquals(new BigDecimal("23.00"), rb.getTotalPoints());
        assertEquals(2, rb.getGamesPlayed());
        assertEquals(new BigDecimal("11.50"), rb.getPointsPerGame());
    }

    @Test
    void lastWeekCountsOnlyTheLatestWeek() {
        BestAvailableResponse response = service.getBestAvailable(GAME, LEAGUE, 1, null, 1, "total", 50);

        assertEquals(3, response.getFromWeek());
        assertEquals(List.of(103, 101, 102), ids(response));
        assertEquals(new BigDecimal("8.00"), response.getPlayers().get(1).getTotalPoints());
        assertEquals(new BigDecimal("3.00"), response.getPlayers().get(2).getTotalPoints());
    }

    @Test
    void windowLongerThanTheSeasonStartsAtWeekOne() {
        BestAvailableResponse response = service.getBestAvailable(GAME, LEAGUE, 5, null, 1, "total", 50);

        assertEquals(1, response.getFromWeek());
        assertEquals(new BigDecimal("40.00"), response.getPlayers().get(0).getTotalPoints());
    }

    @Test
    void sortsByPointsPerGame() {
        BestAvailableResponse response = service.getBestAvailable(GAME, LEAGUE, null, null, 1, "per_game", 50);

        assertEquals(List.of(103, 101, 102), ids(response));
    }

    @Test
    void filtersByPositionMinimumGamesAndLimit() {
        assertEquals(List.of(102), ids(service.getBestAvailable(GAME, LEAGUE, null, "RB", 1, "total", 50)));
        assertEquals(List.of(101, 102), ids(service.getBestAvailable(GAME, LEAGUE, null, null, 2, "total", 50)));
        assertEquals(List.of(101), ids(service.getBestAvailable(GAME, LEAGUE, null, null, 1, "total", 1)));
    }

    @Test
    void leagueWithoutScoringRulesIsNotFound() {
        LeagueDataNotFoundException ex = assertThrows(LeagueDataNotFoundException.class,
                () -> service.getBestAvailable(GAME, 1, null, null, 1, "total", 50));
        assertEquals("Scoring rules for league 470.l.1 aren't loaded", ex.getMessage());
    }

    @Test
    void leagueWithoutAnAvailablePlayersListIsNotFound() {
        jdbc.update("DELETE FROM yahoo_available_players WHERE league_id = ?", LEAGUE);

        LeagueDataNotFoundException ex = assertThrows(LeagueDataNotFoundException.class,
                () -> service.getBestAvailable(GAME, LEAGUE, null, null, 1, "total", 50));
        assertEquals("Available players for league 470.l.523652 aren't loaded", ex.getMessage());
    }

    @Test
    void unknownLeagueIsNotFound() {
        assertThrows(LeagueDataNotFoundException.class,
                () -> service.getBestAvailable(GAME, 999999, null, null, 1, "total", 50));
    }
}
