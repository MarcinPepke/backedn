package com.mpepke.fantasyfootball.repository;

import com.mpepke.fantasyfootball.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);

    boolean existsByUsername(String username);

    Optional<User> findByUsernameIgnoreCase(String username);
}
