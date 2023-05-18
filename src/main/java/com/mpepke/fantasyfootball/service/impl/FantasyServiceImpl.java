package com.mpepke.fantasyfootball.service.impl;

import com.mpepke.fantasyfootball.model.Player;
import com.mpepke.fantasyfootball.model.Ranking;
import com.mpepke.fantasyfootball.model.Team;
import com.mpepke.fantasyfootball.model.User;
import com.mpepke.fantasyfootball.model.player_stats.PlayerStatistic;
import com.mpepke.fantasyfootball.repository.PlayerRepository;
import com.mpepke.fantasyfootball.repository.RankingRepository;
import com.mpepke.fantasyfootball.repository.UserRepository;
import com.mpepke.fantasyfootball.service.FantasyService;
import com.mpepke.fantasyfootball.service.exception.PlayerDoesNotExistsException;
import com.mpepke.fantasyfootball.service.exception.TeamDoesNotExistsException;
import com.mpepke.fantasyfootball.controller.request.TeamUpdateRequest;

import com.mpepke.fantasyfootball.repository.TeamRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@AllArgsConstructor
@Transactional
public class FantasyServiceImpl implements FantasyService {


    private final TeamRepository teamRepository;
    private final PlayerRepository playerRepository;
    private final RankingRepository rankingRepository;


    @Override
    public Team createTeam(User owner, String name) throws Exception {
        List<Team> existingTeams = teamRepository.findAllByOwner(owner);
        for (Team team : existingTeams) {
            if (team.getName().equalsIgnoreCase(name)) {
                throw new IllegalArgumentException("Team name already taken");
            }
        }


        Team team = new Team();
        team.setName(name);
        team.setOwner(owner);
        team.setPlayers(new HashSet<>());
        team.setTotalPoints(0);
        team.setBudget(100); // Example initial budget for the team
        Ranking ranking = Ranking.builder().team(team).build();
        rankingRepository.save(ranking);
        return teamRepository.save(team);
    }

    @Override
    public Team addPlayerToTeam(long teamId, long playerId) throws ChangeSetPersister.NotFoundException {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(ChangeSetPersister.NotFoundException::new);
        Player player = playerRepository.findById(playerId)
                .orElseThrow(ChangeSetPersister.NotFoundException::new);

        if (team.getPlayers().contains(player)) {
            throw new IllegalArgumentException("Player already in the team");
        }

        if (team.getBudget() < player.getPrice()) {
            throw new IllegalArgumentException("Insufficient budget to add the player");
        }

        if (team.getPlayers().size() >= 15) {
            throw new RuntimeException("Maximum number of players reached");
        }

        if (team.getPlayers().stream().filter(p -> p.getPosition().equals(player.getPosition())).count() >= getMaxPlayersForPosition(player.getPosition())) {
            throw new RuntimeException("Maximum number of players for the position reached");
        }

        team.getPlayers().add(player);
        team.setTotalPoints(team.getTotalPoints() - 4);
        team.setBudget(team.getBudget() - player.getAge());
        return teamRepository.save(team);
    }

    @Override
    public Team removePlayerFromTeam(long teamId, long playerId) throws ChangeSetPersister.NotFoundException, TeamDoesNotExistsException, PlayerDoesNotExistsException {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(TeamDoesNotExistsException::new);
        Player player = playerRepository.findById(playerId)
                .orElseThrow(PlayerDoesNotExistsException::new);

        if (!team.getPlayers().contains(player)) {
            throw new IllegalArgumentException("Player not in the team");
        }

        team.getPlayers().remove(player);
        team.setBudget(team.getBudget() + player.getAge());
        return teamRepository.save(team);
    }

    @Override
    public Team updateTeam(User user, TeamUpdateRequest request) {
        Team team = teamRepository.findByOwner(user);

        // Fetch the players from the database using their IDs
//        // Update the team lineup and budget
//        team.setPlayers(players);
//        team.setBudget(request.getBudget());
//
//        System.out.println("players = " + players);
//
//        // Save the updated team to the database
        return teamRepository.save(team);
    }

    private int getMaxPlayersForPosition(String position) {
        return switch (position) {
            case "Goalkeeper" -> 2;
            case "Defender", "Midfielder" -> 5;
            case "Attacker" -> 3;
            default -> throw new IllegalArgumentException("Invalid position");
        };
    }


    public void calculatePlayerPoints(PlayerStatistic.Player player, PlayerStatistic.Statistics stats, Team userTeam) throws Exception {
        int points = 0;

        System.out.println("player.getId() = " + player.getId());

        Optional<Player> playerToCheck = playerRepository.findById((long) player.getId());
        Player player1 = null;

        if (playerToCheck.isPresent())
            player1 = playerToCheck.get();

        System.out.println("player1 = " + player1);

        // Goals scored
        int goalsScored = stats.getGoals().getTotal();
        switch (player1.getPosition()) {
            case "Goalkeeper", "Defender" -> points += goalsScored * 6;
            case "Midfielder" -> points += goalsScored * 5;
            case "Attacker" -> points += goalsScored * 4;
        }

        // Assists
        int assists = stats.getGoals().getAssists();
        points += assists * 3;

        // Clean sheets
        if (player1.getPosition().equals("Goalkeeper") || player1.getPosition().equals("Defender")) {
            int cleanSheets = stats.getGoals().getConceded() < 1 ? 0 : 1;
            if(cleanSheets == 0)
                points+=4;
        } else if (player1.getPosition().equals("Midfielder")) {
            int cleanSheets = stats.getGoals().getConceded() < 1 ? 0 : 1;
            points += cleanSheets;
        }

        // Penalty saves (Goalkeepers only)
        if (player1.getPosition().equals("Goalkeeper")) {
            int penaltySaves = stats.getPenalty().getSaved();
            points += penaltySaves * 5;
        }

        // Penalty misses
        int penaltyMisses = stats.getPenalty().getMissed();
        points -= penaltyMisses * 2;

        // Yellow cards
        int yellowCards = stats.getGoalStats().getYellow();
        points -= yellowCards;

        // Red cards
        int redCards = stats.getGoalStats().getRed();
        points -= redCards * 3;


        // Minutes played
        int minutes = stats.getGames().getMinutes();
        if (minutes <= 60 && minutes > 0)
            points += 1;
        else
            points += 2;

        if (userTeam.getPlayers().contains(player1))
            userTeam.setCurrentGameweekPoints(userTeam.getCurrentGameweekPoints() + points);

        System.out.println("punkty = " + points);
        player1.setCurrentGameWeekPoints(points);
        playerRepository.save(player1);

    }

    @Override
    public Team getUserTeam(User user) {
        return teamRepository.findByOwner(user);
    }

    @Override
    public ResponseEntity<?> getFixture() {
        return null;
    }
}


