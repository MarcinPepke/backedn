package com.mpepke.fantasyfootball.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Lineup {
        private List<Player> Goalkeeper;
        private List<Player> Defender;
        private List<Player> Midfielder;
        private List<Player> Attacker;

}
