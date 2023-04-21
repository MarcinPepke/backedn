package com.mpepke.inzynierka.demo.repository;

import com.mpepke.inzynierka.demo.model.RefreshToken;
import com.mpepke.inzynierka.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);
    Optional<RefreshToken> findByUser(User user);
}
