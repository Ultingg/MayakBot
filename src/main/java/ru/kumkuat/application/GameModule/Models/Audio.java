package ru.kumkuat.application.GameModule.Models;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table
public class Audio {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "audio_generator")
    @SequenceGenerator(name="audio_generator", sequenceName = "audio_id")
    private Long id;
    private String path;
}
