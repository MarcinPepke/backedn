package com.mpepke.inzynierka.demo.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mpepke.inzynierka.demo.model.Fixture;
import com.mpepke.inzynierka.demo.model.Player;
import com.mpepke.inzynierka.demo.model.Team;
import com.mpepke.inzynierka.demo.model.player_stats.PlayerStatistic;
import com.mpepke.inzynierka.demo.model.teamPlayer.TeamPlayer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

public interface SoccerService {

    List<Fixture> getFixturesForSeason(int season, int league) throws URISyntaxException, IOException, InterruptedException;

    Mono<List<PlayerStatistic>> getPlayerStatisticsByFixture(int fixtureId);

    List<Player> getAllPremierLeaguePlayers(int leagueId);

    Mono<List<PlayerStatistic.Player>> getGoalScorersAndAssistProvidersByFixture(int fixtureId, Team userTeam);

    List<Player> getAllPlayers();

    Page<Player> searchPlayers(String name, String position, String club, Double price, Pageable pageable);
}
