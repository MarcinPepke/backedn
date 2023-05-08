package com.mpepke.fantasyfootball.controller;

import com.mpepke.fantasyfootball.controller.request.AddUserRequest;
import com.mpepke.fantasyfootball.model.User;
import com.mpepke.fantasyfootball.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Clock;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    final UserService userService;
    final Clock clock;


    @PostMapping()
    ResponseEntity<User> addUser(@RequestBody AddUserRequest addUserRequest) throws Exception {
        User savedUser = userService.addUser(addUserRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }
}
