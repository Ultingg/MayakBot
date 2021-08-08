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
import ru.kumkuat.application.GameModule.Controller.MailController;
import ru.kumkuat.application.GameModule.Service.BGUserService;

@Slf4j
@Service

public class SendMailCommand extends BotCommand {

    @Autowired
    private MailController mailController;
    @Autowired
    private TemplateEngine templateEngine;
    @Autowired
    private BGUserService bgUserService;

    public SendMailCommand() {
        super("/send_mail", "Направить пользователю письмо на почту");
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
//        if (arguments != null && arguments.length > 0 && arguments[0].equals("all")) {
//            for (var bgUser :
//                    bgUserService.getAllNotNotifedUsers()) {
//                Context context = new Context();
//                context.setVariable("user", bgUser.getFirstName() + " " + bgUser.getSecondName());
//                context.setVariable("starttime", bgUser.getStartTime());
//                var text = templateEngine.process("Emails/Welcome.html", context);
//                mailController.sendSimpleEmail(bgUser.getEmail(), "ProSpectSpb", text);
//                bgUser.setIsNotified(true);
//                bgUserService.setBGUserToDB(bgUser);
//            }
//        }
    }

    void execute(AbsSender sender, SendMessage message, User user) {
        try {
            sender.execute(message);
        } catch (TelegramApiException e) {
        }
    }
}
