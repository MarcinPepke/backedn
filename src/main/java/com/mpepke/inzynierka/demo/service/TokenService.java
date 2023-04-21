package com.mpepke.inzynierka.demo.service;

public interface TokenService {

    String extractUsername(String token);
    String generateAccessToken(String username) throws Exception;
    String generateRefreshToken(String username) throws Exception;
}
