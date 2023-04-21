package com.mpepke.inzynierka.demo.controller.request;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}
