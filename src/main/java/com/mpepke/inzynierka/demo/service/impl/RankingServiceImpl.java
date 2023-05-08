package com.mpepke.inzynierka.demo.service.impl;

import com.mpepke.inzynierka.demo.model.Ranking;
import com.mpepke.inzynierka.demo.repository.RankingRepository;
import com.mpepke.inzynierka.demo.repository.TeamRepository;
import com.mpepke.inzynierka.demo.service.RankingService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class RankingServiceImpl implements RankingService {

    private final RankingRepository rankingRepository;

    @Override
    public List<Ranking> getRankingTotal() {
        return rankingRepository.findAll(Sort.by(Sort.Direction.DESC, "team.totalPoints"));

    }

    @Override
    public List<Ranking> getRankingCurrentGameweek() {
        return rankingRepository.findAll(Sort.by(Sort.Direction.DESC, "team.currentGameweekPoints"));
    }
}
