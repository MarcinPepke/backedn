package com.mpepke.inzynierka.demo.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.mpepke.inzynierka.demo.model.player_stats.PlayerStatistic;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Player {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    private String name;
    private int age;
    private String photo;
    private String nationality;
    private String position;
    private int currentGameWeekPoints;
    private int totalPoints;
    @Column(columnDefinition = "double default 5.3")
    private double price;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "club_id")
    @EqualsAndHashCode.Exclude
    private Club club;

    @ManyToMany
    @EqualsAndHashCode.Exclude
    @JoinTable(name = "Player_teams",
            joinColumns = @JoinColumn(name = "player_id"),
            inverseJoinColumns = @JoinColumn(name = "teams_id"))
    private Set<Team> teams = new LinkedHashSet<>();



    public Set<Team> getTeams() {
        return teams;
    }

    public void setTeams(Set<Team> teams) {
        this.teams = teams;
    }
}
