package com.mpepke.fantasyfootball.repository;

import com.mpepke.fantasyfootball.model.RefreshToken;
import com.mpepke.fantasyfootball.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);
    Optional<RefreshToken> findByUser(User user);
}
