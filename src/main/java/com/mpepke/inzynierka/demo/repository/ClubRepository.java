package com.mpepke.inzynierka.demo.repository;

import com.mpepke.inzynierka.demo.model.Club;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClubRepository extends JpaRepository<Club,Long> {
}
