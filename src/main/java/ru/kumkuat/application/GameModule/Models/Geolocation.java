package ru.kumkuat.application.GameModule.Models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity

public class Geolocation {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "geo_generator")
    @SequenceGenerator(name="geo_generator", sequenceName = "geo_id")
    private Long id;
    //    @Column(columnDefinition = "full_name")
    private String fullName;
    //    @Column(columnDefinition = "latitude")
    private Double latitude;
    //    @Column(columnDefinition = "longitude")
    private Double longitude;


}
