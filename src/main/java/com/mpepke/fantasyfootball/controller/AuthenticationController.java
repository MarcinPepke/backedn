package com.mpepke.fantasyfootball.controller;

import com.mpepke.fantasyfootball.model.dto.AuthenticationDto;
import com.mpepke.fantasyfootball.controller.request.AuthenticationRequest;
import com.mpepke.fantasyfootball.service.TokenService;
import com.mpepke.fantasyfootball.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthenticationController {


    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final UserService userService;

    @PostMapping()
    public ResponseEntity<?> createAuthentication(@RequestBody AuthenticationRequest authenticationRequest) throws Exception {
        String username = userService.loadByUsername(authenticationRequest.getUsername()).orElseThrow(Exception::new).getUsername();
        String token = tokenService.generateAccessToken(authenticationRequest.getUsername());
        String refreshToken = tokenService.generateRefreshToken(authenticationRequest.getUsername());
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(), authenticationRequest.getPassword()));
        return ResponseEntity.ok(new AuthenticationDto(token, refreshToken, username, "ROLE_USER"));
    }
}
