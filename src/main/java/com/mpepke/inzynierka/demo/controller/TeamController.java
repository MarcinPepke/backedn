package com.mpepke.inzynierka.demo.controller;

import com.mpepke.inzynierka.demo.controller.request.CreateTeamRequest;
import com.mpepke.inzynierka.demo.controller.request.TeamUpdateRequest;
import com.mpepke.inzynierka.demo.model.Player;
import com.mpepke.inzynierka.demo.model.Team;
import com.mpepke.inzynierka.demo.model.User;
import com.mpepke.inzynierka.demo.repository.TeamRepository;
import com.mpepke.inzynierka.demo.service.FantasyService;
import com.mpepke.inzynierka.demo.service.TokenService;
import com.mpepke.inzynierka.demo.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/fantasy")
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class TeamController {

    private final TokenService tokenService;
    private final FantasyService fantasyService;
    private final UserService userService;
    private final TeamRepository teamRepository;

    @GetMapping("/team/budget")
    public ResponseEntity<Double> getBudget(HttpServletRequest httpServletRequest) {
        String username = getUsername(httpServletRequest);
        User user = userService.loadByUsername(username).get();
        return ResponseEntity.ok().body(fantasyService.getUserTeam(user).getBudget());
    }

    @PutMapping("/team/save")
    public ResponseEntity<?> savePlayersToDatabase(HttpServletRequest httpServletRequest, @RequestBody TeamUpdateRequest teamUpdateRequest) throws IOException {


        String username = getUsername(httpServletRequest);
        User user = userService.loadByUsername(username).get();
        Map<String, List<Player>> lineup = teamUpdateRequest.getLineup();

        Set<Player> newPlayers = lineup.values().stream()
                .flatMap(List::stream)
                .collect(Collectors.toSet());

        Team byOwner = teamRepository.findByOwner(user);
        byOwner.setPlayers(newPlayers);
        byOwner.setBudget(teamUpdateRequest.getBudget());
        Team save = teamRepository.save(byOwner);

        newPlayers.forEach(player -> System.out.println("player = " + player));
        return ResponseEntity.ok().body(save);

    }

    @PostMapping("/team")
    public Team createTeam(@RequestBody CreateTeamRequest request, HttpServletRequest servletRequest) throws Exception {
        String username = getUsername(servletRequest);
        User user = userService.loadByUsername(username).get();
        return fantasyService.createTeam(user, request.getName());
    }

    @GetMapping("/isTeam")
    public boolean hasTeam(HttpServletRequest httpServletRequest) {
        String username = getUsername(httpServletRequest);
        User user = userService.loadByUsername(username).get();
        Team team = fantasyService.getUserTeam(user);
        System.out.println(team.getOwner() == user);
        return team.getOwner() == user;

    }

    @GetMapping("/user/team")
    public ResponseEntity<Set<Player>> getUserTeam(HttpServletRequest servletRequest) {
        String username = getUsername(servletRequest);
        User user = userService.loadByUsername(username).get();
        Set<Player> players = fantasyService.getUserTeam(user).getPlayers();
        return ResponseEntity.ok().body(players);
    }

    private String getUsername(HttpServletRequest httpServletRequest) {
        String headerValue = httpServletRequest.getHeader("Authorization");
        String jwtToken = headerValue.split(" ")[1];
        return tokenService.extractUsername(jwtToken);
    }
}
