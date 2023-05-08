package com.mpepke.inzynierka.demo.service;

import com.mpepke.inzynierka.demo.controller.request.TeamUpdateRequest;
import com.mpepke.inzynierka.demo.model.Player;
import com.mpepke.inzynierka.demo.model.Team;
import com.mpepke.inzynierka.demo.model.User;
import com.mpepke.inzynierka.demo.model.player_stats.PlayerStatistic;
import com.mpepke.inzynierka.demo.service.exception.PlayerDoesNotExistsException;
import com.mpepke.inzynierka.demo.service.exception.TeamDoesNotExistsException;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface FantasyService {

    Team createTeam(User user, String name) throws Exception;

    Team addPlayerToTeam(long teamId, long playerId) throws ChangeSetPersister.NotFoundException;

    Team removePlayerFromTeam(long teamId, long playerId) throws ChangeSetPersister.NotFoundException, TeamDoesNotExistsException, PlayerDoesNotExistsException;

    Team updateTeam(User user, TeamUpdateRequest request);

    void calculatePlayerPoints(PlayerStatistic.Player player, PlayerStatistic.Statistics stats, Team userTeam) throws Exception;

    Team getUserTeam(User user);

    ResponseEntity<?> getFixture();


}
