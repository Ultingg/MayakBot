package ru.kumkuat.application.GameModule.Commands.MarshakCommands;

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
import ru.kumkuat.application.GameModule.Controller.MailController;

@Slf4j
@Service

public class SendMailCommand extends BotCommand {

    @Autowired
    private MailController mailController;
    @Autowired
    private TemplateEngine templateEngine;

    public SendMailCommand() {
        super("/send_mail", "Направить пользователю письмо на почту");
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        Context context = new Context();
        context.setVariable("user", "value");
        var text = templateEngine.process("Emails/Welcome.html", context);
        mailController.sendSimpleEmail("maximnn1720@gmail.com", "title", text);
    }

    void execute(AbsSender sender, SendMessage message, User user) {
        try {
            sender.execute(message);
        } catch (TelegramApiException e) {
        }
    }
}
