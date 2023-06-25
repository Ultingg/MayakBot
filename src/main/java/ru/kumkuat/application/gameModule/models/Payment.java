package ru.kumkuat.application.gameModule.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
@Getter
@Setter
@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "payment_gen")
    @SequenceGenerator(name = "payment_gen", allocationSize = 1, sequenceName = "payment_gen")
    @Column(name = "id")
    private Long id;
    private Long sum;
    @ManyToOne
    @JoinColumn(name="payer_id", nullable=false)
    private User payer;
    private LocalDateTime paidTime;
    private boolean isPromo;


}
