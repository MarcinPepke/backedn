package com.mpepke.fantasyfootball.service.impl;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mpepke.fantasyfootball.model.Team;
import com.mpepke.fantasyfootball.model.player_stats.PlayerStatistic;
import com.mpepke.fantasyfootball.model.teamPlayer.TeamPlayer;
import com.mpepke.fantasyfootball.service.exception.PlayerDoesNotExistsException;
import com.mpepke.fantasyfootball.model.Club;
import com.mpepke.fantasyfootball.model.Fixture;
import com.mpepke.fantasyfootball.model.Player;
import com.mpepke.fantasyfootball.repository.ClubRepository;
import com.mpepke.fantasyfootball.repository.PlayerRepository;
import com.mpepke.fantasyfootball.service.FantasyService;
import com.mpepke.fantasyfootball.service.SoccerService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class SoccerServiceImpl implements SoccerService {


    private final WebClient webClient;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private ClubRepository clubRepository;

    @Autowired
    private FantasyService fantasyService;

    @Value("${api-football.base-url}")
    String baseUrl;

    @Value("${api-football.api-key}")
    String apiKey;

    public SoccerServiceImpl(@Value("${api-football.base-url}") String baseUrl,
                             @Value("${api-football.api-key}") String apiKey) {
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("x-rapidapi-host", "api-football-v1.p.rapidapi.com")
                .defaultHeader("x-rapidapi-key", apiKey)
                .build();
    }

    @Override
    public List<Fixture> getFixturesForSeason(int season, int league) throws URISyntaxException, IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(baseUrl + "/fixtures?league=" + league + "&season=" + season))
                .header("x-rapidapi-key", apiKey)
                .header("x-rapidapi-host", "api-football-v1.p.rapidapi.com")
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            FixtureResponse fixtureResponse = new ObjectMapper().readValue(response.body(), FixtureResponse.class);
            return fixtureResponse.getResponse();
        }

        return null;
    }

    @Override
    public Mono<List<PlayerStatistic>> getPlayerStatisticsByFixture(int fixtureId) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/fixtures/players")
                        .queryParam("fixture", fixtureId)
                        .build())
                .retrieve()
                .bodyToMono(ApiResponse.class)
                .flatMapIterable(ApiResponse::getResponse)
                .collectList();
    }

    @Override
    public List<Player> getAllPremierLeaguePlayers(int teamId) {
        int totalPages = getTotalPagesOfPlayers(teamId);
        AtomicInteger i = new AtomicInteger();
        List<Flux<TeamPlayer>> playerFluxes = new ArrayList<>();

        for (int page = 1; page <= totalPages; page++) {
            final int currentPage = page;
            Flux<TeamPlayer> teamPlayerFlux = webClient.get()
                    .uri(uriBuilder -> uriBuilder.path("/players")
                            .queryParam("team", teamId)
                            .queryParam("season", "2022")
                            .queryParam("page", currentPage)
                            .build())
                    .retrieve()
                    .bodyToMono(TeamPlayers.class)
                    .flatMapIterable(TeamPlayers::getResponse);
            playerFluxes.add(teamPlayerFlux);

        }
        Flux<TeamPlayer> concat = Flux.concat(playerFluxes);


        List<TeamPlayer> teamPlayers = concat.collectList().block();

        Set<TeamPlayer.TeamPlayerDetails> players = teamPlayers.stream().map(TeamPlayer::getPlayer).collect(Collectors.toSet());
        Optional<TeamPlayer.PlayerTeam> first = teamPlayers.stream().map(teamPlayer -> teamPlayer.getStatistics().get(0).getTeam()).findFirst();
        Club playerClub = new Club(first.get().getId(), first.get().getName(), first.get().getLogo());
        Set<Team> teams = new HashSet<>();
        clubRepository.save(playerClub);
        List<Player> playerSet = players.stream().map(player -> {
            String position = null;
            try {
                position = teamPlayers.stream()
                        .filter(teamPlayer -> teamPlayer.getPlayer().getId() == player.getId())
                        .map(TeamPlayer::getStatistics)
                        .map(stats -> stats.get(0).getGames().getPosition())
                        .findFirst().orElseThrow(PlayerDoesNotExistsException::new);
            } catch (PlayerDoesNotExistsException e) {
                throw new RuntimeException(e);
            }

            System.out.println("player = " + player.getName() + " " + player.getId() + " " + i.getAndIncrement());
            return new Player((long) player.getId(), player.getName(), player.getAge(), player.getPhoto(),
                    player.getNationality(), position, 0, 0, 4.5, playerClub, teams);

        }).collect(Collectors.toList());

        System.out.println("Players before saving: " + playerSet.size());
        for (Player player : playerSet) {
            try {
                Player savedPlayer = playerRepository.save(player);
                if (savedPlayer == null) {
                    System.out.println("Failed to save player: " + player);
                }
            } catch (Exception e) {
                System.out.println("Exception while saving player: " + player);
                e.printStackTrace();
            }
        }


        Set<Long> originalPlayerIds = playerSet.stream().map(Player::getId).collect(Collectors.toSet());
        Set<Long> savedPlayerIds = playerRepository.findAll().stream().map(Player::getId).collect(Collectors.toSet());
        originalPlayerIds.removeAll(savedPlayerIds);
        System.out.println("playerSet = " + playerSet.size());
        System.out.println("Players not saved: " + originalPlayerIds);

        return playerSet.stream().toList();


    }

    @Override
    public Mono<List<PlayerStatistic.Player>> getGoalScorersAndAssistProvidersByFixture(int fixtureId, Team userTeam) {
        return getPlayerStatisticsByFixture(fixtureId)
                .flatMapIterable(Function.identity())
                .flatMap(playerStatistic -> Flux.fromIterable(playerStatistic.getPlayers()))
                .filter(player -> {
                    PlayerStatistic.Statistics stats = player.getStatistics().get(0);
                    try {
                        fantasyService.calculatePlayerPoints(player.getPlayer(), stats, userTeam);

                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    return false;
                })
                .map(PlayerStatistic.Players::getPlayer)
                .collectList();


    }

    @Override
    public List<Player> getAllPlayers() {
        return playerRepository.findAll();
    }

    @Override
    public Page<Player> searchPlayers(String name, String position, String club, Double price, Pageable pageable) {
        Specification<Player> spec = Specification.where(null);

        if (name != null && !name.isEmpty()) {
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
        }
        if (position != null && !position.isEmpty()) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("position"), position));
        }
        if (price != null) {
            spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("price"), price));
        }

        return playerRepository.findAll(spec, pageable);
    }


    public int getTotalPagesOfPlayers(int teamId) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/players")
                        .queryParam("page", 1)
                        .queryParam("team", teamId)
                        .queryParam("season", "2022")
                        .build())
                .retrieve()
                .bodyToMono(ApiResponse.class)
                .map(ApiResponse::getPaging)
                .map(Paging::getTotal)
                .block();
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class FixtureResponse {
        @JsonProperty("response")
        private List<Fixture> response;
    }

    @Data
    public static class ApiResponse {
        @JsonProperty("response")
        private List<PlayerStatistic> response;
        @JsonProperty("paging")
        private Paging paging;
    }

    @Data
    public static class TeamPlayers {
        @JsonProperty("response")
        private List<TeamPlayer> response;
    }

    @Data
    public static class Paging {
        @JsonProperty("current")
        private int current;

        @JsonProperty("total")
        private int total;
    }
}
