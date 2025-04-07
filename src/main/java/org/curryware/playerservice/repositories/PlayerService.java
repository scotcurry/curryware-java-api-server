package org.curryware.playerservice.repositories;

import org.springframework.stereotype.Service;

@Service
public class PlayerService {

    private final PlayerRepository playerRepository;

    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    public Iterable<PlayerRecord> findAll() {
        return playerRepository.findAll(); 
    }

    public PlayerRecord viewPlayerDetails(Integer player_key) {
        return playerRepository.getPlayerDetail(player_key);
    }
}
