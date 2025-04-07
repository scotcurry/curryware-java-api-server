package org.curryware.playerservice.repositories;

public record PlayerRecord(
        String player_id,
        String player_season_key,
        String player_name,
        String player_url,
        String player_team,
        Integer player_bye_week,
        String player_position,
        String player_headshot_url
) { }
