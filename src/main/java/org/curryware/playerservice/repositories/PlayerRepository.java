package org.curryware.playerservice.repositories;

public interface PlayerRepository {

    Iterable<PlayerRecord> findAll();
    PlayerRecord getPlayerDetail(Integer playerKey);
}
