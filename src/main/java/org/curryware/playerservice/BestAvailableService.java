package org.curryware.playerservice;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameterValue;
import org.springframework.stereotype.Service;

import java.sql.Types;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;
import java.util.TreeSet;

/**
 * Ranks a league's available players by fantasy points over recent NFL weeks.
 *
 * Points are calculated in Postgres from nflverse_player_week_stats and the league's
 * league_scoring_value_information, through YahooStatMapping. Players are matched to
 * nflverse by Yahoo id (nflverse_players.yahoo_id for the league's season). The league's
 * available players come from yahoo_available_players, which the curryware-nflverse
 * available-players CronJob replaces every 6 hours.
 */
@Service
public class BestAvailableService {

    private static final Logger logger = LogManager.getLogger(BestAvailableService.class);

    public static final Set<String> POSITIONS = Set.of("QB", "RB", "WR", "TE");
    public static final String SORT_TOTAL = "total";
    public static final String SORT_PER_GAME = "per_game";

    private static final String ORDER_BY_TOTAL = "total_points DESC, points_per_game DESC, av.full_name";
    private static final String ORDER_BY_PER_GAME = "points_per_game DESC, total_points DESC, av.full_name";

    // The league's available Yahoo ids; each query that needs them takes game_id, league_id.
    private static final String AVAILABLE_YAHOO_IDS =
            "SELECT ap.player_id FROM yahoo_available_players ap WHERE ap.game_id = ? AND ap.league_id = ?";

    private final JdbcTemplate jdbcTemplate;

    public BestAvailableService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * @param weeks    the number of most recent regular-season weeks to total, or null for the
     *                 season to date
     * @param position QB, RB, WR or TE, or null for all four
     * @param sort     {@link #SORT_TOTAL} or {@link #SORT_PER_GAME}
     */
    public BestAvailableResponse getBestAvailable(int gameId, int leagueId, Integer weeks, String position,
                                                  int minGames, String sort, int limit) {
        int season = findSeason(gameId, leagueId);
        List<Integer> scoredStatIds = jdbcTemplate.queryForList(
                "SELECT league_stat_id FROM league_scoring_value_information " +
                "WHERE game_id = ? AND league_id = ? AND league_stat_value <> 0",
                Integer.class, gameId, leagueId);
        if (scoredStatIds.isEmpty()) {
            throw new LeagueDataNotFoundException(
                    "Scoring rules for league " + gameId + ".l." + leagueId + " aren't loaded");
        }
        Integer latestWeek = jdbcTemplate.queryForObject(
                "SELECT max(week) FROM nflverse_player_week_stats WHERE season = ? AND season_type = 'REG'",
                Integer.class, season);
        if (latestWeek == null) {
            throw new LeagueDataNotFoundException("No nflverse stats are loaded for the " + season + " season");
        }
        int fromWeek = weeks == null ? 1 : Math.max(1, latestWeek - weeks + 1);

        AvailableSummary available = jdbcTemplate.queryForObject(
                "SELECT count(*) AS players, max(loaded_at) AS loaded_at FROM yahoo_available_players " +
                "WHERE game_id = ? AND league_id = ?",
                (rs, rowNum) -> {
                    OffsetDateTime loadedAt = rs.getObject("loaded_at", OffsetDateTime.class);
                    return new AvailableSummary(rs.getInt("players"), loadedAt == null ? null : loadedAt.toInstant());
                },
                gameId, leagueId);
        if (available == null || available.players() == 0) {
            throw new LeagueDataNotFoundException(
                    "Available players for league " + gameId + ".l." + leagueId + " aren't loaded");
        }

        Integer matched = jdbcTemplate.queryForObject(
                "SELECT count(*) FROM nflverse_players WHERE season = ? AND yahoo_id IN (" + AVAILABLE_YAHOO_IDS + ")",
                Integer.class, season, gameId, leagueId);

        List<Object> params = new ArrayList<>();
        for (YahooStatMapping.Entry entry : YahooStatMapping.ENTRIES) {
            params.add(entry.getStatId());
            params.add(entry.getNflverseColumn());
        }
        params.add(gameId);
        params.add(leagueId);
        params.add(season);
        params.add(gameId);
        params.add(leagueId);
        SqlParameterValue positionParam = new SqlParameterValue(Types.VARCHAR, position);
        params.add(positionParam);
        params.add(positionParam);
        params.add(season);
        params.add(fromWeek);
        params.add(latestWeek);
        params.add(minGames);
        params.add(limit);

        String orderBy = SORT_PER_GAME.equals(sort) ? ORDER_BY_PER_GAME : ORDER_BY_TOTAL;
        List<BestAvailablePlayer> players = jdbcTemplate.query(bestAvailableSql(orderBy), playerRowMapper(),
                params.toArray());

        Set<Integer> unscored = new TreeSet<>(scoredStatIds);
        unscored.removeAll(YahooStatMapping.mappedStatIds());

        BestAvailableResponse response = new BestAvailableResponse();
        response.setGameId(gameId);
        response.setLeagueId(leagueId);
        response.setSeason(season);
        response.setFromWeek(fromWeek);
        response.setToWeek(latestWeek);
        response.setAvailableAsOf(available.loadedAt());
        response.setAvailablePlayerCount(available.players());
        response.setMatchedPlayerCount(matched == null ? 0 : matched);
        response.setUnscoredStatIds(new ArrayList<>(unscored));
        response.setPlayers(players);
        logger.info("Ranked {} best available players for game_id={} league_id={} weeks {}-{} position={}",
                players.size(), gameId, leagueId, fromWeek, latestWeek, position);
        return response;
    }

    private int findSeason(int gameId, int leagueId) {
        List<Integer> seasons = jdbcTemplate.queryForList(
                "SELECT season FROM all_league_information WHERE game_id = ? AND league_id = ?",
                Integer.class, gameId, leagueId);
        if (seasons.isEmpty()) {
            throw new LeagueDataNotFoundException("League " + gameId + ".l." + leagueId + " isn't in all_league_information");
        }
        return seasons.get(0);
    }

    static String bestAvailableSql(String orderBy) {
        StringJoiner statMapRows = new StringJoiner(", ");
        for (int i = 0; i < YahooStatMapping.ENTRIES.size(); i++) {
            statMapRows.add("(?::int, ?::text)");
        }
        return "WITH stat_map (stat_id, nflverse_column) AS (VALUES " + statMapRows + "), " +
               // The league's points per unit of each mapped nflverse column.
               "rules AS (" +
               "  SELECT m.nflverse_column, v.league_stat_value AS points" +
               "  FROM league_scoring_value_information v" +
               "  JOIN stat_map m ON m.stat_id = v.league_stat_id" +
               "  WHERE v.game_id = ? AND v.league_id = ?), " +
               "available AS (" +
               "  SELECT np.gsis_id, np.yahoo_id, np.full_name, np.position, np.team, np.headshot_url" +
               "  FROM nflverse_players np" +
               "  WHERE np.season = ? AND np.yahoo_id IN (" + AVAILABLE_YAHOO_IDS + ")" +
               "    AND (?::text IS NULL OR np.position = ?::text)), " +
               // One row per player per game. Column values are read by name from the row's
               // JSON, so the mapping stays data; a row with no scoring stats still counts as a game.
               "weekly AS (" +
               "  SELECT s.player_id, s.week," +
               "         SUM(COALESCE((j.stats ->> r.nflverse_column)::numeric * r.points, 0)) AS points" +
               "  FROM nflverse_player_week_stats s" +
               "  JOIN available av ON av.gsis_id = s.player_id" +
               "  CROSS JOIN LATERAL (SELECT to_jsonb(s) AS stats) j" +
               "  LEFT JOIN rules r ON TRUE" +
               "  WHERE s.season = ? AND s.season_type = 'REG' AND s.week BETWEEN ? AND ?" +
               "  GROUP BY s.player_id, s.week) " +
               "SELECT av.yahoo_id, av.full_name, av.position, av.team, av.headshot_url," +
               "       ROUND(SUM(w.points), 2) AS total_points," +
               "       COUNT(*) AS games_played," +
               "       ROUND(AVG(w.points), 2) AS points_per_game " +
               "FROM weekly w JOIN available av ON av.gsis_id = w.player_id " +
               "GROUP BY av.yahoo_id, av.full_name, av.position, av.team, av.headshot_url " +
               "HAVING COUNT(*) >= ? " +
               "ORDER BY " + orderBy + " " +
               "LIMIT ?";
    }

    /** How many players yahoo_available_players lists for the league, and when they were loaded. */
    private record AvailableSummary(int players, Instant loadedAt) { }

    private RowMapper<BestAvailablePlayer> playerRowMapper() {
        return (rs, rowNum) -> {
            BestAvailablePlayer player = new BestAvailablePlayer();
            player.setYahooPlayerId(rs.getInt("yahoo_id"));
            player.setFullName(rs.getString("full_name"));
            player.setPosition(rs.getString("position"));
            player.setTeam(rs.getString("team"));
            player.setHeadshotUrl(rs.getString("headshot_url"));
            player.setTotalPoints(rs.getBigDecimal("total_points"));
            player.setGamesPlayed(rs.getInt("games_played"));
            player.setPointsPerGame(rs.getBigDecimal("points_per_game"));
            return player;
        };
    }
}
