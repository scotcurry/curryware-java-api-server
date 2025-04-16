package org.curryware.controllers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.curryware.playerservice.PlayerRecord;
import org.curryware.playerservice.PlayerService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("players")
public class PlayerController {

    private static final Logger logger = LogManager.getLogger(PlayerController.class);
    private final PlayerService playerService;
    
    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }
    
    @GetMapping("/getplayers")
    public @ResponseBody List<PlayerRecord> get() {
        logger.debug("getplayers called");
        return playerService.getPlayerNames();
    }
}
