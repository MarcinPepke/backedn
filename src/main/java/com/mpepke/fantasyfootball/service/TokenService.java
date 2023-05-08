package com.mpepke.fantasyfootball.service;

public interface TokenService {

    String extractUsername(String token);
    String generateAccessToken(String username) throws Exception;
    String generateRefreshToken(String username) throws Exception;
}
