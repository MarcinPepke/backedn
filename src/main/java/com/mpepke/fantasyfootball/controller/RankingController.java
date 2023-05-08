package com.mpepke.fantasyfootball.controller;

import com.mpepke.fantasyfootball.model.Ranking;
import com.mpepke.fantasyfootball.service.RankingService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/fantasy")
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class RankingController {

    private final RankingService rankingService;

    @GetMapping("/ranking-total")
    public ResponseEntity<?> getRankingTotal() {
        List<Ranking> rankingTotal = rankingService.getRankingTotal();
        return ResponseEntity.ok().body(rankingTotal);
    }

    @GetMapping("/ranking-current")
    public ResponseEntity<?> getRankingCurrentGameweek() {
        List<Ranking> rankingTotal = rankingService.getRankingCurrentGameweek();
        return ResponseEntity.ok().body(rankingTotal);
    }
}
