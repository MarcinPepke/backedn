package com.mpepke.fantasyfootball.model.player_stats;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Penalty{
    public int commited;
    public int missed;
    public int saved;
    public int scored;
    public int won;
}
