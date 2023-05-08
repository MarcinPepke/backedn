package com.mpepke.fantasyfootball.service;

import com.mpepke.fantasyfootball.model.User;
import com.mpepke.fantasyfootball.controller.request.AddUserRequest;

import java.util.Optional;

public interface UserService {

    User saveUser(User user);
    Optional<User> loadByUsername(String username);
    User addUser(AddUserRequest addUserRequest) throws Exception;
}
