package ru.kumkuat.application.GameModule.Models;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "bguser")
public class BGUser {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "bg_user_gen")
    @SequenceGenerator(name = "bg_user_gen", allocationSize = 1, sequenceName = "bg_user_seq")
    private Long id;

    private String email;
    private String preferredTime;
    private String telegramUserName;
    private String FirstName;
    private String SecondName;
    private String codeTicket;
    private String startWith;
    private LocalTime startTime;
    private Boolean isNotified;

}
