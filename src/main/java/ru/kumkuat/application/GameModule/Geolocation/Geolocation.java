package ru.kumkuat.application.GameModule.Geolocation;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Table
@Getter
@Setter
@Entity

public class Geolocation {
    @Id
    private Long id;
//    @Column(columnDefinition = "full_name")
    private String fullName;
//    @Column(columnDefinition = "latitude")
    private Double latitude;
//    @Column(columnDefinition = "longitude")
    private Double longitude;



}
