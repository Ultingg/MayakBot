package ru.kumkuat.application.GameModule.Models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Date;

@Getter
@Setter
@Entity
public class TelegramChat {
    @Id
    //@GeneratedValue
    private Long id;
    private String name;
    private Long chatId;
    private boolean isBusy;
    private boolean isStarting;
    private Date startPlayTime;
    private Long userId;
}
