package ru.kumkuat.application.gameModule.promocode.Model;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(name = "promocode_disp")
public class DisposablePromocode {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "promo_disp_generator")
    @SequenceGenerator(name = "promo_log_generator", allocationSize = 1, sequenceName = "promo_disp_seq")
    private Long id;

    @Column(name = "value")
    private String value;

    @Column(name = "is_used")
    private boolean isUsed;

    @Column(name = "promocode_used")
    private LocalDateTime promocodeUsed;

    @Column(name = "is_sent")
    private boolean isSent;

}
