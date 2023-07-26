package ru.kumkuat.application.gameModule.marshakCommands;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import ru.kumkuat.application.gameModule.controller.SimpleEmailService;
import ru.kumkuat.application.gameModule.mail.MailService;
import ru.kumkuat.application.gameModule.promocode.Service.PromocodeService;
import ru.kumkuat.application.gameModule.promocode.Service.TimPadOrderService;

@Slf4j
@Service

public class SendMailCommand extends BotCommand {

    @Autowired
    private SimpleEmailService simpleEmailService;
    @Autowired
    private TemplateEngine templateEngine;
    @Autowired
    private TimPadOrderService timPadOrderService;
    @Autowired
    private PromocodeService promocodeService;

    @Autowired
    private MailService mailService;

    public SendMailCommand() {
        super("/send_mail", "Направить пользователю письмо на почту");
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        if (arguments != null && arguments.length > 0 && arguments[0].equals("all")) {
            for (var timePadOrder :
                    timPadOrderService.getAllNotNotifiedOrders()) {
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
        }
    }

    private String getTextForMessage() {
       return mailService.sendWelcomeMail();
    }


    void execute(AbsSender sender, SendMessage message, User user) {
        try {
            sender.execute(message);
        } catch (TelegramApiException e) {
        }
    }
}
