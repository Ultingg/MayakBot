package ru.kumkuat.application.GameModule.Promocode.Model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "tporder")
public class TimePadOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tp_oreder_gen")
    @SequenceGenerator(name = "tp_oreder_gen", allocationSize = 1, sequenceName = "tp_oreder_gen")
    private Long id;

    private String email;
    private Long orderNumber;
    private String firstName;
    private String lastName;
    private String orderStatus;
    private Integer amountTickets;
    private Boolean isNotified;
}
