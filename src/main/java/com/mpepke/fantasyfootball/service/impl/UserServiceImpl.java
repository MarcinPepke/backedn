package com.mpepke.fantasyfootball.service.impl;

import com.mpepke.fantasyfootball.model.User;
import com.mpepke.fantasyfootball.controller.request.AddUserRequest;
import com.mpepke.fantasyfootball.repository.UserRepository;
import com.mpepke.fantasyfootball.service.UserService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PersistenceContext
    private final EntityManager entityManager;

    @Override
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public Optional<User> loadByUsername(String username) {
        return userRepository.findByUsernameIgnoreCase(username);
    }

    @Override
    public User addUser(AddUserRequest addUserRequest) throws Exception {
        Optional<User> loaded = userRepository.findByUsernameIgnoreCase(addUserRequest.getUsername());
        if (loaded.isPresent())
            throw new Exception();
        else {
            return saveUser(User.builder()
                    .username(addUserRequest.getUsername())
                    .active(addUserRequest.isActive())
                    .password(passwordEncoder.encode(addUserRequest.getPassword()))
                    .role(addUserRequest.getRole())
                    .build());
        }
    }
}
