package ru.kumkuat.application.GameModule.Models;


import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "promocode_log")
public class PromocodeLog {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "promo_log_generator")
    @SequenceGenerator(name = "promo_log_generator", allocationSize = 1, sequenceName = "promo_log_seq")
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "promocode_used")
    private LocalDateTime promocodeUsed;

    @Column(name = "promocode_text")
    private String promocodeText;
}
