package ru.kumkuat.application.GameModule.Models;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Setter
@Getter
@Table
public class Picture {
    @Id
//    @GeneratedValue
//    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "img_generator")
//    @SequenceGenerator(name="img_generator", sequenceName = "img_id")
    private Long id;
    private String path;

}
