package com.mpepke.inzynierka.demo.service;

import com.mpepke.inzynierka.demo.controller.request.AddUserRequest;
import com.mpepke.inzynierka.demo.model.User;

import java.util.Optional;

public interface UserService {

    User saveUser(User user);
    Optional<User> loadByUsername(String username);
    User addUser(AddUserRequest addUserRequest) throws Exception;
}
