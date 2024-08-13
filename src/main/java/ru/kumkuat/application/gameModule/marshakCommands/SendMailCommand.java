package ru.kumkuat.application.gameModule.marshakCommands;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ru.kumkuat.application.gameModule.mail.MailService;
import ru.kumkuat.application.gameModule.mail.SimpleEmailService;

/**
 * Service for sending mail.
 */
@Slf4j
@Service
public class SendMailCommand extends BotCommand {
    private final static Logger logger = LoggerFactory.getLogger(SendChatCommand.class);

    private final SimpleEmailService simpleEmailService;
    private final MailService mailService;

    public SendMailCommand(SimpleEmailService simpleEmailService,
                           MailService mailService) {
        super("/send_mail", "Направить пользователю письмо на почту");
        this.simpleEmailService = simpleEmailService;
        this.mailService = mailService;
    }

    @SneakyThrows
    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        logger.info("SendMail command start");
        if (arguments != null && arguments.length > 0 && arguments[0].equals("all")) {
            int emailSent = simpleEmailService.processMailSending();
            notifyAdmin(absSender, chat, emailSent);
        }
        logger.info("SendMail command finished");
    }

    private void notifyAdmin(AbsSender absSender, Chat chat, int emailSent) throws org.telegram.telegrambots.meta.exceptions.TelegramApiException {
        SendMessage replyMessage = new SendMessage();
        replyMessage.setChatId(chat.getId().toString());
        replyMessage.enableHtml(true);
        String message = emailSent > 0 ?
                String.format("Писем успешно отправлено: %d !", emailSent):
                String.format("Письма не отправлены=!");

        replyMessage.setText(message);
        absSender.execute(replyMessage);
    }

    private String getTextForMessage() {
        return mailService.sendWelcomeMail();
    }
}
