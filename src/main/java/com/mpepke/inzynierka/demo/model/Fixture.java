package com.mpepke.inzynierka.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mpepke.inzynierka.demo.model.player_stats.PlayerStatistic;
import com.mpepke.inzynierka.demo.model.teamPlayer.TeamPlayer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor


public class Fixture {

    @JsonProperty("fixture")
    private FixtureInfo fixture;
    @JsonProperty("teams")
    private TeamInfo teams;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class FixtureInfo{
        @JsonProperty("id")
        private int id;
        @JsonProperty("timestamp")
        private Timestamp timestamp;
    }
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TeamInfo{
        @JsonProperty("home")
        private TeamInfoDetails home;
        @JsonProperty("away")
        private TeamInfoDetails away;
    }
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
        public static class TeamInfoDetails{
        @JsonProperty("id")
        private int id;
        @JsonProperty("name")
        private String name;
        @JsonProperty("logo")
        private String logo;
    }
}
