package org.curryware.controllers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.curryware.playerservice.BestAvailableResponse;
import org.curryware.playerservice.BestAvailableService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Locale;

@RestController
@RequestMapping("/v1/player_info")
public class PlayerController {

    private static final Logger logger = LogManager.getLogger(PlayerController.class);

    static final int MAX_WEEKS = 18;
    static final int MAX_LIMIT = 200;

    private final BestAvailableService bestAvailableService;

    public PlayerController(BestAvailableService bestAvailableService) {
        this.bestAvailableService = bestAvailableService;
    }

    /**
     * Ranks the league's available players by fantasy points under the league's scoring.
     *
     * @param weeks    the most recent N regular-season weeks (1-18); omit for the season to date
     * @param position QB, RB, WR or TE; omit for all four
     * @param minGames leave out players with fewer games in the window
     * @param sort     "total" (default) or "per_game"
     */
    @GetMapping("/get_best_available")
    public BestAvailableResponse getBestAvailable(
            @RequestParam("gameId") int gameId,
            @RequestParam("leagueId") int leagueId,
            @RequestParam(value = "weeks", required = false) Integer weeks,
            @RequestParam(value = "position", required = false) String position,
            @RequestParam(value = "minGames", defaultValue = "1") int minGames,
            @RequestParam(value = "sort", defaultValue = BestAvailableService.SORT_TOTAL) String sort,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        logger.debug("get_best_available called with gameId={} leagueId={} weeks={} position={} minGames={} sort={} limit={}",
                gameId, leagueId, weeks, position, minGames, sort, limit);
        if (weeks != null && (weeks < 1 || weeks > MAX_WEEKS)) {
            throw new IllegalArgumentException("weeks must be between 1 and " + MAX_WEEKS);
        }
        String normalizedPosition = position == null || position.isBlank()
                ? null : position.trim().toUpperCase(Locale.ROOT);
        if (normalizedPosition != null && !BestAvailableService.POSITIONS.contains(normalizedPosition)) {
            throw new IllegalArgumentException("position must be one of QB, RB, WR, TE");
        }
        if (minGames < 1) {
            throw new IllegalArgumentException("minGames must be at least 1");
        }
        if (!BestAvailableService.SORT_TOTAL.equals(sort) && !BestAvailableService.SORT_PER_GAME.equals(sort)) {
            throw new IllegalArgumentException("sort must be total or per_game");
        }
        if (limit < 1 || limit > MAX_LIMIT) {
            throw new IllegalArgumentException("limit must be between 1 and " + MAX_LIMIT);
        }
        return bestAvailableService.getBestAvailable(gameId, leagueId, weeks, normalizedPosition, minGames, sort, limit);
    }
}
