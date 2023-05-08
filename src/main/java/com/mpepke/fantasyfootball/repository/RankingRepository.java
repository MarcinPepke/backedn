package com.mpepke.fantasyfootball.repository;

import com.mpepke.fantasyfootball.model.Ranking;
import com.mpepke.fantasyfootball.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RankingRepository extends JpaRepository<Ranking, Long> {
    Optional<Ranking> findByTeam(Team team);



}
