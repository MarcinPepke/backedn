package com.mpepke.fantasyfootball.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.Hibernate;

import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@Table(name = "ranking")
@AllArgsConstructor
@Builder(toBuilder = true)
public class Ranking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @OneToOne(orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "team_id")
    private Team team;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Ranking ranking = (Ranking) o;
        return id != null && Objects.equals(id, ranking.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
