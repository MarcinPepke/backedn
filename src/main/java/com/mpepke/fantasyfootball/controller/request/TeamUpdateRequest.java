package com.mpepke.fantasyfootball.controller.request;

import com.mpepke.fantasyfootball.model.Player;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TeamUpdateRequest {
    private Map<String, List<Player>> lineup;
    private double budget;


}
