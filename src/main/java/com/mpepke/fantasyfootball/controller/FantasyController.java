package com.mpepke.fantasyfootball.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.mpepke.fantasyfootball.model.Mapping;
import com.mpepke.fantasyfootball.service.FantasyService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/api/fantasy")
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class FantasyController {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final FantasyService fantasyService;
    private final ObjectMapper objectMapper;


    @GetMapping("/fixture/")
    public ResponseEntity<?> getFixture() {
        return fantasyService.getFixture();
    }


    @SneakyThrows
    @GetMapping("/gameweek")
    public ResponseEntity<?> getAllMatchesInCurrentGameWeek() {
        var now = LocalDate.now();

        var previousSaturday = now.with(TemporalAdjusters.previous(DayOfWeek.SATURDAY));
        var nextSaturday = now.with(TemporalAdjusters.next(DayOfWeek.FRIDAY));

        HttpRequest fixturesInRangeRequest = HttpRequest.newBuilder()
                .uri(URI.create("https://api-football-v1.p.rapidapi.com/v3/fixtures?league=39&season=2022&from=%s&to=%s".formatted(previousSaturday.format(DATE_FORMATTER), nextSaturday.format(DATE_FORMATTER))))
                .header("X-RapidAPI-Key", "e1b0c02686msh82422e7444fd399p1209a8jsnf0d186d59c85")
                .header("X-RapidAPI-Host", "api-football-v1.p.rapidapi.com")
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();

        HttpResponse<String> fixturesInRangeResponse =
                HttpClient.newHttpClient().send(fixturesInRangeRequest, HttpResponse.BodyHandlers.ofString());
        JsonNode fixturesJson = objectMapper.readTree(fixturesInRangeResponse.body());

        List<com.mpepke.fantasyfootball.model.Mapping.Game> games = StreamSupport.stream(fixturesJson.get("response").spliterator(), false)
                .map(gameJson -> {
                    try {
                        return objectMapper.treeToValue(gameJson, com.mpepke.fantasyfootball.model.Mapping.Game.class);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                }).toList();
        // System.out.println("games = " + games);

        for (com.mpepke.fantasyfootball.model.Mapping.Game game : games) {
            if (Instant.now().getEpochSecond() < game.getFixture().getTimestamp()) {
                continue;
            }


            // System.out.println("playerStatistics = " + playerStatistics);
        }

        return ResponseEntity.ok(games);
    }

    @SneakyThrows
    @GetMapping("/gameweek/{round}")
    public ResponseEntity<?> getAllMatchForGivenGameweek(@PathVariable String round) {


        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api-football-v1.p.rapidapi.com/v3/fixtures?league=39&season=2022&round=Regular%20Season%20-%20" + round))
                .header("content-type", "application/octet-stream")
                .header("X-RapidAPI-Key", "e1b0c02686msh82422e7444fd399p1209a8jsnf0d186d59c85")
                .header("X-RapidAPI-Host", "api-football-v1.p.rapidapi.com")
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());

        HttpResponse<String> fixturesInRangeResponse =
                HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        JsonNode fixturesJson = objectMapper.readTree(fixturesInRangeResponse.body());

        List<com.mpepke.fantasyfootball.model.Mapping.Game> games = StreamSupport.stream(fixturesJson.get("response").spliterator(), false)
                .map(gameJson -> {
                    try {
                        return objectMapper.treeToValue(gameJson, Mapping.Game.class);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                }).toList();
        System.out.println(games);
        return ResponseEntity.ok(games);
    }

    @SneakyThrows
    @GetMapping("/gameweek/currentRound")
    public ResponseEntity<?> getCurrentRoundInfo() {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api-football-v1.p.rapidapi.com/v3/fixtures/rounds?league=39&season=2022&current=true"))
                .header("content-type", "application/octet-stream")
                .header("X-RapidAPI-Key", "e1b0c02686msh82422e7444fd399p1209a8jsnf0d186d59c85")
                .header("X-RapidAPI-Host", "api-football-v1.p.rapidapi.com")
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());

        HttpResponse<String> fixturesInRangeResponse =
                HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        JsonNode fixturesJson = objectMapper.readTree(fixturesInRangeResponse.body());

        List<Response> games = StreamSupport.stream(fixturesJson.get("response").spliterator(), false)
                .map(gameJson -> {
                    try {
                        return objectMapper.treeToValue(gameJson, Response.class);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                }).collect(Collectors.toList());
        System.out.println(games);
        return ResponseEntity.ok(games);
    }


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Response {
        public String round;
    }

}
