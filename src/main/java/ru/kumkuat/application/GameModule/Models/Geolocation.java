package ru.kumkuat.application.GameModule.Models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Getter
@Setter
@Entity

public class Geolocation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    //    @Column(columnDefinition = "full_name")
    private String fullName;
    //    @Column(columnDefinition = "latitude")
    private Double latitude;
    //    @Column(columnDefinition = "longitude")
    private Double longitude;


}
