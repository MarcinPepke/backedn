package com.mpepke.fantasyfootball.model.player_stats;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlayerStatistic {

    @JsonProperty("players")
    private List<Players> players;

    @JsonProperty("team")
    private Team team;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Players{
        @JsonProperty("player")
        private Player player;
        @JsonProperty("statistics")
        private List<Statistics> statistics;
    }
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Player {
        private int id;
        private String name;
        private String photo;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Team {
        private int id;
        private String name;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Fixture {
        @JsonProperty("fixture")
        private int id;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Statistics {
        @JsonProperty("cards")
        private Cards goalStats;
        @JsonProperty("games")
        private Games games;
        @JsonProperty("goals")
        private Goals goals;
        @JsonProperty("passes")
        private Passes passes;
        @JsonProperty("penalty")
        private Penalty penalty;
    }

}
