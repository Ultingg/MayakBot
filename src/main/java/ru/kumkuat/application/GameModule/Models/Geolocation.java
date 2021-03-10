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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    //    @Column(columnDefinition = "full_name")
    private String fullName;
    //    @Column(columnDefinition = "latitude")
    private Double latitude;
    //    @Column(columnDefinition = "longitude")
    private Double longitude;


}
