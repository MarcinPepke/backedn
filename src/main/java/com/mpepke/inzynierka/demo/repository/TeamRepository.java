package com.mpepke.inzynierka.demo.repository;

import com.mpepke.inzynierka.demo.model.Team;
import com.mpepke.inzynierka.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeamRepository extends JpaRepository<Team,Long> {


    List<Team> findAllByOwner(User user);

    Team findByOwner(User user);

}
