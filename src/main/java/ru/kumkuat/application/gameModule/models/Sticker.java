package ru.kumkuat.application.gameModule.models;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Data

public class Sticker {

    @Id
    private Long id;
    private String path;
}
