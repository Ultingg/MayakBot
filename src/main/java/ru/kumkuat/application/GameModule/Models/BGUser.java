package ru.kumkuat.application.GameModule.Models;

import lombok.Getter;
import lombok.Setter;


import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "bguser")
public class BGUser {
    @Id
    @GeneratedValue (strategy = GenerationType.SEQUENCE, generator = "bg_user_gen")
    @SequenceGenerator(name = "bg_user_gen", allocationSize = 1, sequenceName = "bg_user_seq")
    private Long id;

    private String email;
    private LocalDateTime timeOfStart;
    private String preferredTime;
    private String telegramUserName;
    private String codeTicket;
    private String startWith;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

}
