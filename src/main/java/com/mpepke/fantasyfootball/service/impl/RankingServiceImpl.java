package com.mpepke.fantasyfootball.service.impl;

import com.mpepke.fantasyfootball.model.Ranking;
import com.mpepke.fantasyfootball.repository.RankingRepository;
import com.mpepke.fantasyfootball.service.RankingService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
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
