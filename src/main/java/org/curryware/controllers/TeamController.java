package org.curryware.controllers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.curryware.teamservice.TeamInfoRecord;
import org.curryware.teamservice.TeamInfoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("team_info")
public class TeamController {

    private static final Logger logger = LogManager.getLogger(TeamController.class);
    private final TeamInfoService teamInfoService;

    public TeamController(TeamInfoService teamInfoService) {
        this.teamInfoService = teamInfoService;
    }

    @GetMapping("/get_team_info")
    public @ResponseBody List<TeamInfoRecord> getTeamInfo(
            @RequestParam("gameId") String gameId,
            @RequestParam("leagueId") String leagueId) {
        logger.debug("get_team_info called with gameId={} leagueId={}", gameId, leagueId);
        return teamInfoService.getTeamsByLeague(gameId, leagueId);
    }
}
