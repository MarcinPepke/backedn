package com.mpepke.fantasyfootball.controller.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddUserRequest {
    private String username;
    private String password;
    private boolean active = true;
    private String role = "ROLE_USER";

}
