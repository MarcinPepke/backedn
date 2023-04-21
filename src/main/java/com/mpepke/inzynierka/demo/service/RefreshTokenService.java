package com.mpepke.inzynierka.demo.service;

import com.mpepke.inzynierka.demo.model.RefreshToken;
import com.mpepke.inzynierka.demo.model.User;

import java.util.Optional;

public interface RefreshTokenService {

    Optional<RefreshToken> findByUser(User user);

    RefreshToken save(RefreshToken refreshToken);

    Optional<RefreshToken> findByToken(String token);

    void deleteToken(RefreshToken refreshToken);
}
