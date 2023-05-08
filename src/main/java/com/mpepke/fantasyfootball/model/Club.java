package com.mpepke.fantasyfootball.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Data
@AllArgsConstructor
@ToString
@NoArgsConstructor
public class Club {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    private String name;
    private String logo;

}
