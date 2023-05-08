package com.mpepke.fantasyfootball.repository;

import com.mpepke.fantasyfootball.model.Team;
import com.mpepke.fantasyfootball.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeamRepository extends JpaRepository<Team,Long> {


    List<Team> findAllByOwner(User user);

    Team findByOwner(User user);

}
