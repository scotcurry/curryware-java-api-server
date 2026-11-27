package org.curryware.playerservice;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

class YahooStatMappingTest {

    /**
     * A misspelled column would score 0 points without any error, because the SQL reads
     * columns by name. Checked against the table's DDL when the schema repo is beside this one.
     */
    @Test
    void everyMappedColumnExistsInTheStatsTable() throws IOException {
        Path ddl = Path.of(System.getenv().getOrDefault("CURRYWARE_SCHEMA_DIR", "../curryware-fantasy-database"))
                .resolve("create_nflverse_player_week_stats_table.sql");
        assumeTrue(Files.isRegularFile(ddl), ddl + " not found");
        String sql = Files.readString(ddl);
        for (YahooStatMapping.Entry entry : YahooStatMapping.ENTRIES) {
            assertTrue(Pattern.compile("(?m)^\\s+" + entry.getNflverseColumn() + "\\s").matcher(sql).find(),
                    entry.getNflverseColumn() + " (stat " + entry.getStatId() + ") is not a column of nflverse_player_week_stats");
        }
    }

    @Test
    void mapsTheOffensiveStatsVerifiedAgainstNflversePpr() {
        assertEquals(16, YahooStatMapping.ENTRIES.size());
        assertEquals(java.util.Set.of(4, 5, 6, 9, 10, 11, 12, 13, 15, 16, 18, 57), YahooStatMapping.mappedStatIds());
    }
}
