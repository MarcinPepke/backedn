package com.mpepke.fantasyfootball.service;

import com.mpepke.fantasyfootball.model.Ranking;

import java.util.List;

public interface RankingService {


    List<Ranking> getRankingTotal();

    List<Ranking> getRankingCurrentGameweek();

}
