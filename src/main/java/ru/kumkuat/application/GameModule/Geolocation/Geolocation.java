package ru.kumkuat.application.GameModule.Geolocation;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table
public class Geolocation {
    @Id
    private Long id;
    @Column(columnDefinition = "full_name")
    private String fullName;
    @Column(columnDefinition = "latitude")
    private Double latitude;
    @Column(columnDefinition = "longitude")
    private Double longitude;



}
