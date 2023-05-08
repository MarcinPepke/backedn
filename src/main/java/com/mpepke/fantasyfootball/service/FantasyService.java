package com.mpepke.fantasyfootball.service;

import com.mpepke.fantasyfootball.model.Team;
import com.mpepke.fantasyfootball.model.player_stats.PlayerStatistic;
import com.mpepke.fantasyfootball.service.exception.PlayerDoesNotExistsException;
import com.mpepke.fantasyfootball.controller.request.TeamUpdateRequest;
import com.mpepke.fantasyfootball.model.User;
import com.mpepke.fantasyfootball.service.exception.TeamDoesNotExistsException;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.ResponseEntity;

public interface FantasyService {

    Team createTeam(User user, String name) throws Exception;

    Team addPlayerToTeam(long teamId, long playerId) throws ChangeSetPersister.NotFoundException;

    Team removePlayerFromTeam(long teamId, long playerId) throws ChangeSetPersister.NotFoundException, TeamDoesNotExistsException, PlayerDoesNotExistsException;

    Team updateTeam(User user, TeamUpdateRequest request);

    void calculatePlayerPoints(PlayerStatistic.Player player, PlayerStatistic.Statistics stats, Team userTeam) throws Exception;

    Team getUserTeam(User user);

    ResponseEntity<?> getFixture();


}
