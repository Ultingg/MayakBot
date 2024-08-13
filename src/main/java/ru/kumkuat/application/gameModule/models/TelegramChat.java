package ru.kumkuat.application.gameModule.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Entity
public class TelegramChat {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "chat_user_gen")
    @SequenceGenerator(name = "chat_user_gen", allocationSize = 1, sequenceName = "chat_user_gen")
    private Long id;
    private String name;
    private Long chatId;
    private boolean isBusy;
    private boolean isStarting;
    private Date startPlayTime;
    private Long userId;
}
