package com.mpepke.fantasyfootball.service;

import com.mpepke.fantasyfootball.model.RefreshToken;
import com.mpepke.fantasyfootball.model.User;

import java.util.Optional;

public interface RefreshTokenService {

    Optional<RefreshToken> findByUser(User user);

    RefreshToken save(RefreshToken refreshToken);

    Optional<RefreshToken> findByToken(String token);

    void deleteToken(RefreshToken refreshToken);
}
