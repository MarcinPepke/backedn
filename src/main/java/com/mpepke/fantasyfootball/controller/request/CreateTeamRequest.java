package com.mpepke.fantasyfootball.controller.request;

import lombok.Data;

@Data
public class CreateTeamRequest {
    private String name;
    private Long ownerId;

}
