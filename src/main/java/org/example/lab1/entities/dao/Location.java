package org.example.lab1.entities.dao;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.lab1.entities.dto.LocationDTO;

@Entity
@Table(name = "location")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Location {
    @Id
    @Column(
            name = "id",
            unique = true,
            nullable = false
    )
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "x", nullable = false)
    private Double x; //Поле не может быть null

    @Column(name = "y", nullable = false)
    private Float y; //Поле не может быть null

    @Column(name = "name", length = 871)
    private String name; //Длина строки не должна быть больше 871, Поле может быть null

    public LocationDTO toDTO() {
        return new LocationDTO(this.id, this.x, this.y, this.name);
    }
}
