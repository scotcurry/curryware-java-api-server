package org.curryware.playerservice;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * Calls the available-players Cloud Function. It queries Yahoo and takes about 17 seconds for
 * a league (1,047 players), so callers should go through AvailablePlayersCache.
 */
@Component
public class AvailablePlayersClient {

    private static final Logger logger = LogManager.getLogger(AvailablePlayersClient.class);
    private static final Duration CONNECT_TIMEOUT = Duration.ofSeconds(10);
    private static final Duration READ_TIMEOUT = Duration.ofSeconds(90);

    private final RestClient restClient;

    public AvailablePlayersClient(
            @Value("${curryware.available-players.url:https://us-central1-currywareff.cloudfunctions.net/available-players}")
            String url) {
        HttpClient httpClient = HttpClient.newBuilder().connectTimeout(CONNECT_TIMEOUT).build();
        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(httpClient);
        requestFactory.setReadTimeout(READ_TIMEOUT);
        this.restClient = RestClient.builder().baseUrl(url).requestFactory(requestFactory).build();
    }

    /** Returns the Yahoo ids of the league's available players. */
    public List<Integer> fetchAvailablePlayerIds(int gameId, int leagueId) {
        long start = System.nanoTime();
        List<AvailablePlayer> players = restClient.get()
                .uri(builder -> builder.queryParam("game_id", gameId).queryParam("league_id", leagueId).build())
                .retrieve()
                .body(new ParameterizedTypeReference<List<AvailablePlayer>>() { });
        if (players == null) {
            throw new IllegalStateException("Available-players response had no body");
        }
        List<Integer> ids = new ArrayList<>(players.size());
        for (AvailablePlayer player : players) {
            try {
                ids.add(Integer.parseInt(player.getPlayerId()));
            } catch (NumberFormatException | NullPointerException ex) {
                logger.warn("Skipping available player with non-numeric id {} ({})",
                        player.getPlayerId(), player.getFullName());
            }
        }
        logger.info("Fetched {} available players for game_id={} league_id={} in {} ms",
                ids.size(), gameId, leagueId, Duration.ofNanos(System.nanoTime() - start).toMillis());
        return ids;
    }
}
