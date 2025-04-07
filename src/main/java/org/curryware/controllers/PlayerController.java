package org.curryware.controllers;

import org.curryware.playerservice.repositories.PlayerRecord;
import org.curryware.playerservice.repositories.PlayerService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("players")
public class PlayerController {
    private final PlayerService playerService;
    
    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }
    
    @GetMapping("/getplayers")
    public @ResponseBody Iterable<PlayerRecord> get() {
        return playerService.findAll();
    }

    @GetMapping("/player/{player_key}")
    public @ResponseBody PlayerRecord getPlayerByKey(@PathVariable Integer player_key) {
        return playerService.viewPlayerDetails(player_key);
    }
}
