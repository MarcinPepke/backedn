package com.mpepke.fantasyfootball.model.dto;

import lombok.Getter;

@Getter
public class AuthenticationDto extends RefreshTokenDto{

    private final String username;
    private final String role;

    public AuthenticationDto(String accessToken, String refreshToken, String username, String role) {
        super(accessToken,refreshToken);
        this.username = username;
        this.role = role;
    }
}
