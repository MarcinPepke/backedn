package com.mpepke.inzynierka.demo.model.teamPlayer;


import lombok.Data;

@Data
public class TeamPlayerDto {

    private int id;
    private String name;
    private int age;
    private String nationality;
    private String photo;
    private Long teamId;
    private String teamName;
    private String logo;

    public TeamPlayerDto(TeamPlayer teamPlayer) {
        this.id = teamPlayer.getPlayer().getId();
        this.name = teamPlayer.getPlayer().getName();
        this.age = teamPlayer.getPlayer().getAge();
        this.nationality = teamPlayer.getPlayer().getNationality();
        this.photo = teamPlayer.getPlayer().getPhoto();
        this.teamId = teamPlayer.getStatistics().get(0).getTeam().getId();
        this.teamName = teamPlayer.getStatistics().get(0).getTeam().getName();
        this.logo = teamPlayer.getStatistics().get(0).getTeam().getLogo();
    }
}
