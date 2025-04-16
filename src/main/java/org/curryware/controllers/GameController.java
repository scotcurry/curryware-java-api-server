package org.curryware.controllers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.curryware.gameservice.GameInfoRecord;
import org.curryware.gameservice.GameInfoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("game_info")
public class GameController {

    private static final Logger logger = LogManager.getLogger(GameController.class);
    private final GameInfoService gameInfoService;

    public GameController(GameInfoService gameInfoService) {
        this.gameInfoService = gameInfoService;
    }

    @GetMapping("/get_game_info")
    public @ResponseBody List<GameInfoRecord> get() {
        logger.debug("get_team_info called");
        return gameInfoService.getCurrentGames();
    }
}
