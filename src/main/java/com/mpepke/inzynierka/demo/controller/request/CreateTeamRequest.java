package com.mpepke.inzynierka.demo.controller.request;

import com.mpepke.inzynierka.demo.model.User;
import lombok.Data;

@Data
public class CreateTeamRequest {
    private String name;
    private Long ownerId;

}
