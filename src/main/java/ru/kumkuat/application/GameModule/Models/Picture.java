package ru.kumkuat.application.GameModule.Models;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Setter
@Getter
@Table
public class Picture {
    @Id
    private Long id;
    private String path;

}
