package ru.kumkuat.application.gameModule.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.kumkuat.application.gameModule.models.Payment;
import ru.kumkuat.application.gameModule.repository.PaymentRepository;

import java.time.LocalDateTime;

@Slf4j
@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final UserService userService;
    private static final int MINOR_DELIMITER = 100;

    public PaymentService(PaymentRepository paymentRepository, UserService userService) {
        this.paymentRepository = paymentRepository;
        this.userService = userService;
    }

    public void processPayment(Update update) {
        Long telegramId = update.getMessage().getFrom().getId();
        int sum = update.getMessage().getSuccessfulPayment().getTotalAmount() / MINOR_DELIMITER;

        Payment payment = new Payment();
        payment.setPaidTime(LocalDateTime.now());
        ru.kumkuat.application.gameModule.models.User client = userService.getUserByTelegramId(telegramId);
        payment.setSum((long) sum);
        payment.setPromo(client.isPromo());
        payment.setPayer(client);
        long paymentId = 0;
        try {
            paymentId = savePayment(payment);
        } catch (Exception e) {
            log.error("Error while saving payment of user id: {}", telegramId);
        }
        log.info("Payment saved by id {} for user: {}, paid sum {}.", paymentId, telegramId, sum);
    }

    private Long savePayment(Payment payment) {
        return paymentRepository.save(payment).getId();
    }
}
