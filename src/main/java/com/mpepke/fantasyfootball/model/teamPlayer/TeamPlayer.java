package com.mpepke.fantasyfootball.model.teamPlayer;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mpepke.fantasyfootball.model.player_stats.Games;
import lombok.Data;

import java.util.List;

@Data
public class TeamPlayer {
    @JsonProperty("player")
    private TeamPlayerDetails player;
    @JsonProperty("statistics")
    private List<PlayerInfo> statistics;

    @Data
    public static class TeamPlayerDetails {
        @JsonProperty("id")
        private int id;
        @JsonProperty("name")
        private String name;
        @JsonProperty("age")
        private int age;
        @JsonProperty("nationality")
        private String nationality;
        @JsonProperty("photo")
        private String photo;
    }

    @Data
    public static class PlayerInfo {
        @JsonProperty("team")
        private PlayerTeam team;
        @JsonProperty("games")
        private Games games;


    }

    @Data
    public static class PlayerTeam {
        @JsonProperty("id")
        private Long id;
        @JsonProperty("name")
        private String name;
        @JsonProperty("logo")
        private String logo;
    }
}
