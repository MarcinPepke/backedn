package com.mpepke.fantasyfootball.controller;


import com.mpepke.fantasyfootball.model.Team;
import com.mpepke.fantasyfootball.model.player_stats.PlayerStatistic;
import com.mpepke.fantasyfootball.service.FantasyService;
import com.mpepke.fantasyfootball.service.SoccerService;
import com.mpepke.fantasyfootball.model.Fixture;
import com.mpepke.fantasyfootball.model.Player;
import com.mpepke.fantasyfootball.model.User;
import com.mpepke.fantasyfootball.service.TokenService;
import com.mpepke.fantasyfootball.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/player")

public class PlayerStatisticController {


    private final SoccerService soccerService;
    private final TokenService tokenService;
    private final UserService userService;

    private final FantasyService fantasyService;

    @GetMapping("/fixtures/{season}/{league}")
    public ResponseEntity<List<Fixture>> getFixturesForSeason(@PathVariable int season, @PathVariable int league) throws URISyntaxException, IOException, InterruptedException {
        List<Fixture> fixturesForSeason = soccerService.getFixturesForSeason(season, league);

        return ResponseEntity.ok().body(fixturesForSeason);
    }

    @GetMapping()
    public Mono<ResponseEntity<List<PlayerStatistic>>> getFixturesForPlayer(@RequestParam int fixtureId) {
        return soccerService.getPlayerStatisticsByFixture(fixtureId)
                .map(playerStatistics -> ResponseEntity.ok().body(playerStatistics))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/team")
    public List<Player> getAllClubPlayers(@RequestParam int teamId) {
        return soccerService.getAllPremierLeaguePlayers(teamId);
    }

    @GetMapping("/stats")
    public Mono<ResponseEntity<List<PlayerStatistic.Player>>> getPlayerStata(@RequestParam int fixtureId, HttpServletRequest httpServletRequest) {
        String username = getUsername(httpServletRequest);
        User user = userService.loadByUsername(username).get();
        Team userTeam = fantasyService.getUserTeam(user);

        return soccerService.getGoalScorersAndAssistProvidersByFixture(fixtureId, userTeam)
                .map(players -> ResponseEntity.ok().body(players))
                .defaultIfEmpty(ResponseEntity.notFound().build());

    }

    private String getUsername(HttpServletRequest httpServletRequest) {
        String headerValue = httpServletRequest.getHeader("Authorization");
        String jwtToken = headerValue.split(" ")[1];
        return tokenService.extractUsername(jwtToken);
    }

    @GetMapping("/all")
    public ResponseEntity<Page<Player>> searchPlayers(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String position,
            @RequestParam(required = false) Double price,
            @RequestParam(required = false) String club,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Player> players = soccerService.searchPlayers(name, position, club, price, pageable);
        return ResponseEntity.ok(players);
    }


}
