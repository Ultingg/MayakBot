package ru.kumkuat.application.gameModule.api.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import ru.kumkuat.application.gameModule.mail.SimpleEmailService;
import ru.kumkuat.application.gameModule.promocode.Model.TimePadOrder;
import ru.kumkuat.application.gameModule.promocode.Service.PromocodeService;
import ru.kumkuat.application.gameModule.promocode.Service.TimePadOrderService;

import java.util.ArrayList;
import java.util.List;
@Slf4j
@Service
public class EmailService {


    private final SimpleEmailService simpleEmailService;
    private final TemplateEngine templateEngine;
    private final PromocodeService promocodeService;

    private final TimePadOrderService timePadOrderService;

    public EmailService(SimpleEmailService simpleEmailService, TemplateEngine templateEngine, PromocodeService promocodeService,
                        TimePadOrderService timePadOrderService) {
        this.simpleEmailService = simpleEmailService;
        this.templateEngine = templateEngine;
        this.promocodeService = promocodeService;
        this.timePadOrderService = timePadOrderService;
    }


    public List<String> sendMail() {
        log.info("Sending emails for timePadOrders started.");
        List<String> emailSent = new ArrayList<>();
        for (TimePadOrder timePadOrder : timePadOrderService.getAllNotNotifiedOrders()) {
            int amountOfLetters = timePadOrder.getAmountTickets();
            for (int i = 0; i < amountOfLetters; i++) {
                Context context = new Context();
                context.setVariable("user", timePadOrder.getFirstName() + " " + timePadOrder.getLastName());
                context.setVariable("promocode", promocodeService.getDisposalPromocode().getValue());
                String text = templateEngine.process("Emails/WelcomeCode.html", context);
                simpleEmailService.sendSimpleEmail(timePadOrder.getEmail(), "Важная информация для старта спектакля «Проспект Поэтов»", text);

                emailSent.add(timePadOrder.getEmail());
            }
            timePadOrder.setIsNotified(true);
            timePadOrderService.save(timePadOrder);
        }
        log.info("Sending emails for timePadOrders finished.");
        return emailSent;
    }


}
