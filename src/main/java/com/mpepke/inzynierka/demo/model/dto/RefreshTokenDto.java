package com.mpepke.inzynierka.demo.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RefreshTokenDto {
    private String accessToken;
    private String refreshToken;
}
