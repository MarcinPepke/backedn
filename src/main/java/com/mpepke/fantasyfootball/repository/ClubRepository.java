package com.mpepke.fantasyfootball.repository;

import com.mpepke.fantasyfootball.model.Club;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClubRepository extends JpaRepository<Club,Long> {
}
