package com.mpepke.inzynierka.demo.model;

import com.mpepke.inzynierka.demo.model.player_stats.PlayerStatistic;

public class PointCalculator {
    public static int calculatePoints(PlayerStatistic fixturePlayer, Player player) {
        int points = 0;

        // Goals
       // points += calculateGoalPoints(player.getPosition(), fixturePlayer.getGoals());

      /*  // Assists
        points += fixturePlayer() * 3;

        // Clean sheets
        points += calculateCleanSheetPoints(player.getPosition(), fixturePlayer.isCleanSheet());

        // Yellow cards
        points -= fixturePlayer.getYellowCards();

        // Red cards
        points -= fixturePlayer.getRedCards() * 3;

        // Penalty saved
        if (player.getPosition().equals("Goalkeeper") && fixturePlayer.getPenaltySaved() > 0) {
            points += 5 * fixturePlayer.getPenaltySaved();
        }*/

        return points;
    }

    private static int calculateGoalPoints(String position, int goals) {
        int pointsPerGoal = switch (position) {
            case "Forward" -> 4;
            case "Midfielder" -> 5;
            case "Defender" -> 6;
            default -> 0;
        };

        return pointsPerGoal * goals;
    }

    private static int calculateCleanSheetPoints(String position, boolean cleanSheet) {
        if (!cleanSheet) {
            return 0;
        }

        return switch (position) {
            case "Goalkeeper", "Defender" -> 4;
            case "Midfielder" -> 1;
            default -> 0;
        };
    }
}
