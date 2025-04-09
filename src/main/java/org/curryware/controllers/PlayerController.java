package org.curryware.controllers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.curryware.currywarejavaapiserver.CurrywareJavaApiServerApplication;
import org.curryware.playerservice.repositories.PlayerRecord;
import org.curryware.playerservice.repositories.PlayerService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("players")
public class PlayerController {

    private static final Logger logger = LogManager.getLogger(PlayerController.class);
    private final PlayerService playerService;
    
    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }
    
    @GetMapping("/getplayers")
    public @ResponseBody Iterable<PlayerRecord> get() {
        logger.debug("getplayers called");
        return playerService.findAll();
    }

    @GetMapping("/player/{player_key}")
    public @ResponseBody PlayerRecord getPlayerByKey(@PathVariable Integer player_key) {
        return playerService.viewPlayerDetails(player_key);
    }
}
