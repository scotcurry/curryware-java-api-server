package org.curryware.playerservice;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Maps Yahoo stat ids (league_scoring_value_information.league_stat_id) to the
 * nflverse_player_week_stats columns that count them. Yahoo stat ids are the same in every
 * league and season. A stat can sum several columns (2-point conversions, fumbles lost).
 *
 * Verified against nflverse: scoring every 2026 row with standard PPR values through this
 * mapping reproduced nflverse's own fantasy_points_ppr exactly (3,599 of 3,599 rows).
 *
 * Only offensive player stats are mapped. Kicking (19-30) and team defense (31-56, 82) stats
 * have no matching rows, because nflverse_players holds only QB, RB, WR and TE; the response
 * lists the league's unmapped stat ids so the gap is visible.
 */
public final class YahooStatMapping {

    public static final class Entry {
        private final int statId;
        private final String nflverseColumn;

        Entry(int statId, String nflverseColumn) {
            this.statId = statId;
            this.nflverseColumn = nflverseColumn;
        }

        public int getStatId() { return statId; }
        public String getNflverseColumn() { return nflverseColumn; }
    }

    public static final List<Entry> ENTRIES = List.of(
            new Entry(4, "passing_yards"),
            new Entry(5, "passing_tds"),
            new Entry(6, "passing_interceptions"),
            new Entry(9, "rushing_yards"),
            new Entry(10, "rushing_tds"),
            new Entry(11, "receptions"),
            new Entry(12, "receiving_yards"),
            new Entry(13, "receiving_tds"),
            new Entry(15, "special_teams_tds"),
            new Entry(16, "passing_2pt_conversions"),
            new Entry(16, "rushing_2pt_conversions"),
            new Entry(16, "receiving_2pt_conversions"),
            new Entry(18, "sack_fumbles_lost"),
            new Entry(18, "rushing_fumbles_lost"),
            new Entry(18, "receiving_fumbles_lost"),
            new Entry(57, "fumble_recovery_tds"));

    private YahooStatMapping() {
    }

    public static Set<Integer> mappedStatIds() {
        Set<Integer> ids = new LinkedHashSet<>();
        for (Entry entry : ENTRIES) {
            ids.add(entry.getStatId());
        }
        return ids;
    }
}
