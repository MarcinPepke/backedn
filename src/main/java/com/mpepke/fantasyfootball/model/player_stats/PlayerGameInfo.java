package com.mpepke.fantasyfootball.model.player_stats;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlayerGameInfo {

    private int playerId;
    private int goals;
    private int assists;
    private int yellow;
    private int red;
    private int cleanSheet;
    private int missedPenalty;
    private int savedPenalty;

}
