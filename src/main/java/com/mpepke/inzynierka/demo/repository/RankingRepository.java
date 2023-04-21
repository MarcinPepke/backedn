package com.mpepke.inzynierka.demo.repository;

import com.mpepke.inzynierka.demo.model.Ranking;
import com.mpepke.inzynierka.demo.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RankingRepository extends JpaRepository<Ranking, Long> {
    Optional<Ranking> findByTeam(Team team);



}
