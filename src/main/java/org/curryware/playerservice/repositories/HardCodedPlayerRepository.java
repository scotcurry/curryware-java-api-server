package org.curryware.playerservice.repositories;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class HardCodedPlayerRepository implements PlayerRepository {

    private static final List<PlayerRecord> playerList = new ArrayList<>();

    @Override
    public Iterable<PlayerRecord> findAll() {

        for (int counter = 0; counter < 10; counter++) {
            PlayerRecord player = getPlayerRecord(counter);
            playerList.add(player);
        }

        return playerList;
    }

    @Override
    public PlayerRecord getPlayerDetail(Integer playerKey) {
        return playerList.get(playerKey);
    }

    private PlayerRecord getPlayerRecord(int counter) {

        return switch (counter) {
            case 0 -> new PlayerRecord("29235", "449.p.29235", "Jared Goff",
                    "https://sports.yahoo.com/nfl/players/29235", "Det", 5, "QB",
                    "https://s.yimg.com/iu/api/res/1.2/1rxier.hfP.y4ZFXrMFnTg--~C/YXBwaWQ9eXNwb3J0cztjaD0yMzM2O2NyPTE7Y3c9MTc5MDtkeD04NTc7ZHk9MDtmaT11bGNyb3A7aD02MDtxPTEwMDt3PTQ2/https://s.yimg.com/xe/i/us/sp/v/nfl_cutout/players_l/08132024/29235.png");
            case 1 -> new PlayerRecord("30972", "449.p.30972", "Saquon Barkley",
                    "https://sports.yahoo.com/nfl/players/30972", "Phi", 5, "RB",
                    "https://s.yimg.com/iu/api/res/1.2/EIvjz2UZqtJeL0id_t8O9w--~C/YXBwaWQ9eXNwb3J0cztjaD0yMzM2O2NyPTE7Y3c9MTc5MDtkeD04NTc7ZHk9MDtmaT11bGNyb3A7aD02MDtxPTEwMDt3PTQ2/https://s.yimg.com/xe/i/us/sp/v/nfl_cutout/players_l/03142024/30972.1.png");
            case 2 -> new PlayerRecord("26686", "449.p.26686", "Travis Kelce",
                    "https://sports.yahoo.com/nfl/players/26686", "KC", 6, "TE",
                    "https://s.yimg.com/iu/api/res/1.2/FZegSWhmoJv4Kc1ZIo_f6A--~C/YXBwaWQ9eXNwb3J0cztjaD0yMzM2O2NyPTE7Y3c9MTc5MDtkeD04NTc7ZHk9MDtmaT11bGNyb3A7aD02MDtxPTEwMDt3PTQ2/https://s.yimg.com/xe/i/us/sp/v/nfl_cutout/players_l/09112024/26686.png");
            case 3 -> new PlayerRecord("26658", "449.p.26658", "Zach Ertz",
                    "https://sports.yahoo.com/nfl/players/26658", "Was", 14, "TE",
                    "https://s.yimg.com/iu/api/res/1.2/qdH8Fxuy9AHNHt3e0qRKBA--~C/YXBwaWQ9eXNwb3J0cztjaD0yMzM2O2NyPTE7Y3c9MTc5MDtkeD04NTc7ZHk9MDtmaT11bGNyb3A7aD02MDtxPTEwMDt3PTQ2/https://s.yimg.com/xe/i/us/sp/v/nfl_cutout/players_l/09162024/26658.png");
            case 4 -> new PlayerRecord("31005", "449.p.31005", "Nick Chubb",
                    "https://sports.yahoo.com/nfl/players/31005", "Cle", 10, "RB",
                    "https://s.yimg.com/iu/api/res/1.2/IThJZ_NpD6YSFFsCy0mlSw--~C/YXBwaWQ9eXNwb3J0cztjaD0yMzM2O2NyPTE7Y3c9MTc5MDtkeD04NTc7ZHk9MDtmaT11bGNyb3A7aD02MDtxPTEwMDt3PTQ2/https://s.yimg.com/xe/i/us/sp/v/nfl_cutout/players_l/07232024/31005.png");
            case 5 -> new PlayerRecord("30295", "449.p.30295", "Aaron Jones Sr.",
                    "https://sports.yahoo.com/nfl/players/30295", "Min", 6, "RB",
                    "https://s.yimg.com/iu/api/res/1.2/ghlj5iCwATjRx_DP1qJh9w--~C/YXBwaWQ9eXNwb3J0cztjaD0yMzM2O2NyPTE7Y3c9MTc5MDtkeD04NTc7ZHk9MDtmaT11bGNyb3A7aD02MDtxPTEwMDt3PTQ2/https://s.yimg.com/xe/i/us/sp/v/nfl_cutout/players_l/07172024/30295.png");
            case 6 -> new PlayerRecord("32687", "449.p.32687", "CeeDee Lamb",
                    "https://sports.yahoo.com/nfl/players/32687", "Dal", 7, "WR",
                    "https://s.yimg.com/iu/api/res/1.2/m7roijAJm1gBr0UmWhCElQ--~C/YXBwaWQ9eXNwb3J0cztjaD0yMzM2O2NyPTE7Y3c9MTc5MDtkeD04NTc7ZHk9MDtmaT11bGNyb3A7aD02MDtxPTEwMDt3PTQ2/https://s.yimg.com/xe/i/us/sp/v/nfl_cutout/players_l/07312024/32687.png");
            case 7 -> new PlayerRecord("32692", "449.p.32692", "Justin Jefferson",
                    "https://sports.yahoo.com/nfl/players/32692", "Min", 6, "WR",
                    "https://s.yimg.com/iu/api/res/1.2/6SnCKhIZ1ch9lAL2wYgKoA--~C/YXBwaWQ9eXNwb3J0cztjaD0yMzM2O2NyPTE7Y3c9MTc5MDtkeD04NTc7ZHk9MDtmaT11bGNyb3A7aD02MDtxPTEwMDt3PTQ2/https://s.yimg.com/xe/i/us/sp/v/nfl_cutout/players_l/09202024/32692.1.png");
            case 8 -> new PlayerRecord("31002", "449.p.31002", "Lamar Jackson",
                    "https://sports.yahoo.com/nfl/players/31002", "Bal", 14, "QB",
                    "https://s.yimg.com/iu/api/res/1.2/g10WJ8RahCoBorjOb36Nlg--~C/YXBwaWQ9eXNwb3J0cztjaD0yMzM2O2NyPTE7Y3c9MTc5MDtkeD04NTc7ZHk9MDtmaT11bGNyb3A7aD02MDtxPTEwMDt3PTQ2/https://s.yimg.com/xe/i/us/sp/v/nfl_cutout/players_l/07252024/31002.png");
            case 9 -> new PlayerRecord("9265", "449.p.9265", "Matthew Stafford",
                    "https://sports.yahoo.com/nfl/players/9265", "LAR", 6, "QB",
                    "https://s.yimg.com/iu/api/res/1.2/KvaEtf4vjh9oqrXF_l1iIw--~C/YXBwaWQ9eXNwb3J0cztjaD0yMzM2O2NyPTE7Y3c9MTc5MDtkeD04NTc7ZHk9MDtmaT11bGNyb3A7aD02MDtxPTEwMDt3PTQ2/https://s.yimg.com/xe/i/us/sp/v/nfl_cutout/players_l/09162024/9265.png");
            default -> new PlayerRecord("0", "0", "0", "0", "0", 0, "0", "0");
        };
    }
}
