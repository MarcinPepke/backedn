package com.mpepke.fantasyfootball.service;

import com.mpepke.fantasyfootball.model.Team;
import com.mpepke.fantasyfootball.model.player_stats.PlayerStatistic;
import com.mpepke.fantasyfootball.model.Fixture;
import com.mpepke.fantasyfootball.model.Player;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
