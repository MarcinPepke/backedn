package com.mpepke.inzynierka.demo.service;

import com.mpepke.inzynierka.demo.model.Ranking;

import java.util.List;

public interface RankingService {


    List<Ranking> getRankingTotal();

    List<Ranking> getRankingCurrentGameweek();

}
