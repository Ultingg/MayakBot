package ru.kumkuat.application.gameModule.promocode.Model;

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
    private String time;

    @Override
    public String toString() {
        return "TimePadOrder{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", orderNumber=" + orderNumber +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", orderStatus='" + orderStatus + '\'' +
                ", amountTickets=" + amountTickets +
                ", isNotified=" + isNotified +
                ", time='" + time + '\'' +
                '}';
    }
}
