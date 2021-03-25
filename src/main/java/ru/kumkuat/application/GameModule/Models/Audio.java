package ru.kumkuat.application.GameModule.Models;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@Table
public class Audio {
    @Id
    private Long id;
    private String path;
}
