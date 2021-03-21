package ru.kumkuat.application.GameModule.Models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_generator")
    @SequenceGenerator(name="user_generator", sequenceName = "user_id")
    private Long id;
    private String name;
    private Long sceneId;
    private Long telegramUserId;
}
