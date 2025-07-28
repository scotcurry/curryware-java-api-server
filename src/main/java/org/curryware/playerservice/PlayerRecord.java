package org.curryware.playerservice;

public class PlayerRecord {
        private String player_id;
        private String player_season_key;
        private String player_name;
        private String player_url;
        private String player_team;
        private Integer player_bye_week;
        private Integer player_uniform_number;
        private String player_position;
        private String player_headshot;

        public String getPlayer_id() {
            return player_id;
        }
        public String getPlayer_season_key() {
            return player_season_key;
        }
        public String getPlayer_name() {
            return player_name;
        }
        public String getPlayer_url() {
            return player_url;
        }
        public String getPlayer_team() {
            return player_team;
        }
        public Integer getPlayer_bye_week() {
            return player_bye_week;
        }
        public Integer getPlayer_uniform_number() { return player_uniform_number;}
        public String getPlayer_position() {
            return player_position;
        }
        public String getPlayer_headshot() {
            return player_headshot;
        }

        public void setPlayer_id(String player_id) {
            this.player_id = player_id;
        }
        public void setPlayer_season_key(String player_season_key) {
            this.player_season_key = player_season_key;
        }
        public void setPlayer_name(String player_name) {
            this.player_name = player_name;
        }
        public void setPlayer_url(String player_url) {
            this.player_url = player_url;
        }
        public void setPlayer_team(String player_team) {
            this.player_team = player_team;
        }
        public void setPlayer_bye_week(Integer player_bye_week) {
            this.player_bye_week = player_bye_week;
        }
        public void setPlayer_uniform_number(Integer player_uniform_number) { this.player_uniform_number = player_uniform_number;}
        public void setPlayer_position(String player_position) {
            this.player_position = player_position;
        }
        public void setPlayer_headshot(String player_headshot) {
            this.player_headshot = player_headshot;
        }
}
