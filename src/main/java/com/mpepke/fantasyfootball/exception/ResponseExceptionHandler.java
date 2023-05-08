package com.mpepke.fantasyfootball.exception;


import com.mpepke.fantasyfootball.service.exception.PlayerDoesNotExistsException;
import com.mpepke.fantasyfootball.service.exception.TeamDoesNotExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ResponseExceptionHandler {

    @ExceptionHandler(TeamDoesNotExistsException.class)
    public ResponseEntity<?> handleTeamDoesNotExistException(){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Team does not exists");
    }
    @ExceptionHandler(PlayerDoesNotExistsException.class)
    public ResponseEntity<?> handlePlayerDoesNotExistException(){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Player does not exists");
    }
}
