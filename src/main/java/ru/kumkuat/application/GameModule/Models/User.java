package ru.kumkuat.application.GameModule.Models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue (strategy = GenerationType.SEQUENCE, generator = "user_gen")
    @SequenceGenerator(name = "user_gen", allocationSize = 1, sequenceName = "user_seq")
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "scene_id")
    private Long sceneId;

    @Column(name = "telegram_user_id")
    private Long telegramUserId;

    @Column(name = "is_admin")
    private boolean isAdmin;

    @Column(name = "has_pay")
    private boolean hasPay;

    @Column(name = "nick_name")
    private String nickName;

    @Column(name = "is_triggered")
    private boolean isTriggered;

    @Column(name = "is_playing")
    private boolean isPlaying;

    @Column(name = "is_promo")
    private boolean isPromo;

    @Column(name = "registration_stamp")
    private LocalDateTime registrationStamp;

}
