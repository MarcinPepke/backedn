package com.mpepke.inzynierka.demo.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mpepke.inzynierka.demo.controller.request.AddPlayerRequest;
import com.mpepke.inzynierka.demo.controller.request.CreateTeamRequest;
import com.mpepke.inzynierka.demo.controller.request.RemovePlayerRequest;
import com.mpepke.inzynierka.demo.controller.request.TeamUpdateRequest;
import com.mpepke.inzynierka.demo.model.*;
import com.mpepke.inzynierka.demo.model.player_stats.PlayerStatistic;
import com.mpepke.inzynierka.demo.repository.PlayerRepository;
import com.mpepke.inzynierka.demo.repository.RankingRepository;
import com.mpepke.inzynierka.demo.repository.TeamRepository;
import com.mpepke.inzynierka.demo.service.FantasyService;
import com.mpepke.inzynierka.demo.service.RankingService;
import com.mpepke.inzynierka.demo.service.TokenService;
import com.mpepke.inzynierka.demo.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Bean;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/api/fantasy")
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class FantasyController {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final FantasyService fantasyService;
    private final UserService userService;

    private final TokenService tokenService;
    private final ObjectMapper objectMapper;
    private final RankingRepository rankingRepository;
    private final PlayerRepository playerRepository;
    private final TeamRepository teamRepository;
    private final RankingService rankingService;

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

    private String getUsername(HttpServletRequest httpServletRequest) {
        String headerValue = httpServletRequest.getHeader("Authorization");
        String jwtToken = headerValue.split(" ")[1];
        return tokenService.extractUsername(jwtToken);
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

    @PutMapping("/team/{teamId}/addPlayer")
    public Team addPlayerToTeam(@PathVariable int teamId, @RequestBody AddPlayerRequest request) throws ChangeSetPersister.NotFoundException {
        return fantasyService.addPlayerToTeam(teamId, request.getPlayerId());
    }

    @PutMapping("/team/{teamId}/removePlayer")
    public Team removePlayerFromTeam(@PathVariable int teamId, @RequestBody RemovePlayerRequest request) throws ChangeSetPersister.NotFoundException {
        return fantasyService.removePlayerFromTeam(teamId, request.getPlayerId());
    }

    @GetMapping("/fixture/")
    public ResponseEntity<?> getFixture() {
        return fantasyService.getFixture();
    }


    @GetMapping("/ranking-total")
    public ResponseEntity<?> getRankingTotal() {
        List<Ranking> rankingTotal = rankingService.getRankingTotal();
        return ResponseEntity.ok().body(rankingTotal);
    }

    @GetMapping("/ranking-current")
    public ResponseEntity<?> getRankingCurrentGameweek() {
        List<Ranking> rankingTotal = rankingService.getRankingCurrentGameweek();
        return ResponseEntity.ok().body(rankingTotal);
    }

    @SneakyThrows
    @GetMapping("/gameweek")
    public ResponseEntity<?> getAllMatchesInCurrentGameWeek() {
        var now = LocalDate.now();

        var previousSaturday = now.with(TemporalAdjusters.previous(DayOfWeek.SATURDAY));
        var nextSaturday = now.with(TemporalAdjusters.next(DayOfWeek.FRIDAY));

        HttpRequest fixturesInRangeRequest = HttpRequest.newBuilder()
                .uri(URI.create("https://api-football-v1.p.rapidapi.com/v3/fixtures?league=39&season=2022&from=%s&to=%s".formatted(previousSaturday.format(DATE_FORMATTER), nextSaturday.format(DATE_FORMATTER))))
                .header("X-RapidAPI-Key", "e1b0c02686msh82422e7444fd399p1209a8jsnf0d186d59c85")
                .header("X-RapidAPI-Host", "api-football-v1.p.rapidapi.com")
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> fixturesInRangeResponse =
                HttpClient.newHttpClient().send(fixturesInRangeRequest, HttpResponse.BodyHandlers.ofString());
        JsonNode fixturesJson = objectMapper.readTree(fixturesInRangeResponse.body());

        List<Mapping.Game> games = StreamSupport.stream(fixturesJson.get("response").spliterator(), false)
                .map(gameJson -> {
                    try {
                        return objectMapper.treeToValue(gameJson, Mapping.Game.class);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                }).toList();
        // System.out.println("games = " + games);

        for (Mapping.Game game : games) {
            if (Instant.now().getEpochSecond() < game.getFixture().getTimestamp()) {
                continue;
            }


            // System.out.println("playerStatistics = " + playerStatistics);
        }

        return ResponseEntity.ok(games);
    }

    public static class Mapping {
        @Data
        public static class Away {
            public int id;
            public String name;
            public String logo;
            public boolean winner;
        }

        @Data
        public static class Extratime {
            public Object home;
            public Object away;
        }

        @Data
        public static class Fixture {
            public int id;
            public String referee;
            public String timezone;
            public OffsetDateTime date;
            public int timestamp;
            public Periods periods;
            public Venue venue;
            public Status status;
        }

        @Data
        public static class Fulltime {
            public int home;
            public int away;
        }

        @Data
        public static class Goals {
            public int home;
            public int away;
        }

        @Data
        public static class Halftime {
            public int home;
            public int away;
        }

        @Data
        public static class Home {
            public int id;
            public String name;
            public String logo;
            public boolean winner;
        }

        @Data
        public static class League {
            public int id;
            public String name;
            public String country;
            public String logo;
            public String flag;
            public int season;
            public String round;
        }

        @Data
        public static class Penalty {
            public Object home;
            public Object away;
        }

        @Data
        public static class Periods {
            public int first;
            public int second;
        }

        @Data
        public static class Game {
            public Fixture fixture;
            public League league;
            public Teams teams;
            public Goals goals;
            public Score score;
        }

        @Data
        public static class Score {
            public Halftime halftime;
            public Fulltime fulltime;
            public Extratime extratime;
            public Penalty penalty;
        }

        @Data
        public static class Status {
            @JsonProperty("long")
            public String mylong;
            @JsonProperty("short")
            public String myshort;
            public int elapsed;
        }

        @Data
        public static class Teams {
            public Home home;
            public Away away;
        }

        @Data
        public static class Venue {
            public int id;
            public String name;
            public String city;
        }


    }
}
