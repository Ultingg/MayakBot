package ru.kumkuat.application.gameModule.repository;

import org.springframework.data.repository.CrudRepository;
import ru.kumkuat.application.gameModule.models.Payment;

public interface PaymentRepository extends CrudRepository<Payment, Long> {
}
