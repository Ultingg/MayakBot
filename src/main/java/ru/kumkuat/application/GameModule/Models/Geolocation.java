package ru.kumkuat.application.GameModule.Models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table
public class Geolocation {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "geo_generator")
    @SequenceGenerator(name="geo_generator", sequenceName = "geo_id")
    private Long id;
    private String fullName;
    private Double latitude;
    private Double longitude;


}
