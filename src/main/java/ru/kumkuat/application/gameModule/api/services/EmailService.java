package ru.kumkuat.application.gameModule.api.services;

import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import ru.kumkuat.application.gameModule.controller.SimpleEmailService;
import ru.kumkuat.application.gameModule.promocode.Model.TimePadOrder;
import ru.kumkuat.application.gameModule.promocode.Service.PromocodeService;
import ru.kumkuat.application.gameModule.promocode.Service.TimPadOrderService;

@Service
public class EmailService {


    private final SimpleEmailService simpleEmailService;
    private final TemplateEngine templateEngine;
    private final PromocodeService promocodeService;

    private final TimPadOrderService timPadOrderService;

    public EmailService(SimpleEmailService simpleEmailService, TemplateEngine templateEngine, PromocodeService promocodeService,
                        TimPadOrderService timPadOrderService) {
        this.simpleEmailService = simpleEmailService;
        this.templateEngine = templateEngine;
        this.promocodeService = promocodeService;
        this.timPadOrderService = timPadOrderService;
    }


    public String sendMail() {
        for (TimePadOrder timePadOrder : timPadOrderService.getAllNotNotifiedOrders()) {
            int amountOfLetters = timePadOrder.getAmountTickets();
            for (int i = 0; i < amountOfLetters; i++) {
                Context context = new Context();
                context.setVariable("user", timePadOrder.getFirstName() + " " + timePadOrder.getLastName());
                context.setVariable("promocode", promocodeService.getDisposalPromocode().getValue());
                String text = templateEngine.process("Emails/WelcomeCode.html", context);
                simpleEmailService.sendSimpleEmail(timePadOrder.getEmail(), "Важная информация для старта спектакля «Проспект Поэтов»", text);
            }
            timePadOrder.setIsNotified(true);
            timPadOrderService.save(timePadOrder);
        }
        return "success";
    }


}
