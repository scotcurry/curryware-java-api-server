package org.curryware.controllers;

import org.curryware.playerservice.AvailablePlayersUnavailableException;
import org.curryware.playerservice.BestAvailablePlayer;
import org.curryware.playerservice.BestAvailableResponse;
import org.curryware.playerservice.BestAvailableService;
import org.curryware.playerservice.LeagueDataNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PlayerControllerTest {

    private static final String URL = "/v1/player_info/get_best_available";

    private BestAvailableService service;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        service = mock(BestAvailableService.class);
        mockMvc = MockMvcBuilders.standaloneSetup(new PlayerController(service))
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    private static BestAvailableResponse sampleResponse() {
        BestAvailablePlayer player = new BestAvailablePlayer();
        player.setYahooPlayerId(40881);
        player.setFullName("Drake Maye");
        player.setPosition("QB");
        player.setTeam("NE");
        player.setTotalPoints(new BigDecimal("40.00"));
        player.setGamesPlayed(3);
        player.setPointsPerGame(new BigDecimal("13.33"));
        BestAvailableResponse response = new BestAvailableResponse();
        response.setGameId(470);
        response.setLeagueId(523652);
        response.setSeason(2026);
        response.setFromWeek(10);
        response.setToWeek(12);
        response.setAvailableAsOf(Instant.parse("2026-11-27T12:00:00Z"));
        response.setAvailablePlayerCount(1047);
        response.setMatchedPlayerCount(480);
        response.setUnscoredStatIds(List.of(19, 20));
        response.setPlayers(List.of(player));
        return response;
    }

    @Test
    void returnsTheRankedPlayers() throws Exception {
        when(service.getBestAvailable(470, 523652, 3, "RB", 2, "per_game", 25)).thenReturn(sampleResponse());

        mockMvc.perform(get(URL).param("gameId", "470").param("leagueId", "523652").param("weeks", "3")
                        .param("position", "rb").param("minGames", "2").param("sort", "per_game").param("limit", "25"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.season").value(2026))
                .andExpect(jsonPath("$.fromWeek").value(10))
                .andExpect(jsonPath("$.toWeek").value(12))
                .andExpect(jsonPath("$.availablePlayerCount").value(1047))
                .andExpect(jsonPath("$.matchedPlayerCount").value(480))
                .andExpect(jsonPath("$.unscoredStatIds[0]").value(19))
                .andExpect(jsonPath("$.players[0].yahooPlayerId").value(40881))
                .andExpect(jsonPath("$.players[0].totalPoints").value(40.00))
                .andExpect(jsonPath("$.players[0].gamesPlayed").value(3));
    }

    @Test
    void defaultsToTheSeasonToDateForAllPositions() throws Exception {
        when(service.getBestAvailable(eq(470), eq(523652), isNull(), isNull(), eq(1), eq("total"), eq(50)))
                .thenReturn(sampleResponse());

        mockMvc.perform(get(URL).param("gameId", "470").param("leagueId", "523652"))
                .andExpect(status().isOk());
        verify(service).getBestAvailable(470, 523652, null, null, 1, "total", 50);
    }

    @Test
    void rejectsInvalidParametersWith400() throws Exception {
        String[][] invalid = {
                {"weeks", "0"}, {"weeks", "19"}, {"position", "K"}, {"minGames", "0"},
                {"sort", "points"}, {"limit", "0"}, {"limit", "201"}, {"weeks", "three"}};
        for (String[] param : invalid) {
            mockMvc.perform(get(URL).param("gameId", "470").param("leagueId", "523652").param(param[0], param[1]))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.error").exists());
        }
        verifyNoInteractions(service);
    }

    @Test
    void missingLeagueIdIs400() throws Exception {
        mockMvc.perform(get(URL).param("gameId", "470"))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(service);
    }

    @Test
    void missingLeagueDataIs404WithTheReason() throws Exception {
        when(service.getBestAvailable(anyInt(), anyInt(), any(), any(), anyInt(), anyString(), anyInt()))
                .thenThrow(new LeagueDataNotFoundException("Scoring rules for league 470.l.523652 aren't loaded"));

        mockMvc.perform(get(URL).param("gameId", "470").param("leagueId", "523652"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Scoring rules for league 470.l.523652 aren't loaded"));
    }

    @Test
    void unavailableYahooListIs502() throws Exception {
        when(service.getBestAvailable(anyInt(), anyInt(), any(), any(), anyInt(), anyString(), anyInt()))
                .thenThrow(new AvailablePlayersUnavailableException("timed out", new RuntimeException()));

        mockMvc.perform(get(URL).param("gameId", "470").param("leagueId", "523652"))
                .andExpect(status().isBadGateway());
    }

    @Test
    void databaseFailureIs503() throws Exception {
        when(service.getBestAvailable(anyInt(), anyInt(), any(), any(), anyInt(), anyString(), anyInt()))
                .thenThrow(new DataAccessResourceFailureException("connection refused"));

        mockMvc.perform(get(URL).param("gameId", "470").param("leagueId", "523652"))
                .andExpect(status().isServiceUnavailable());
    }
}
