package ru.kumkuat.application.GameModule.Models;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
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
    private String inviteLink;
    private boolean isBusy;
    private Date startPlayTime;
    private Long userId;
}
