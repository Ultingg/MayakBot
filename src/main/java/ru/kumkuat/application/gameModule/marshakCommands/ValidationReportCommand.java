package ru.kumkuat.application.gameModule.marshakCommands;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.kumkuat.application.gameModule.models.BGUser;
import ru.kumkuat.application.gameModule.service.BGUserService;
import ru.kumkuat.application.gameModule.service.UserService;
import ru.kumkuat.application.gameModule.service.XLSXReportValidationService;

import java.util.List;
@Slf4j
@Component
public class ValidationReportCommand extends BotCommand implements AdminCommand {


    private static final String COMMAND_IDENTIFIER = "/bgreport";
    private static final String COMMAND_DESCRIPTION = "Вывести список не валидированных участников";
    private static final String EXTENDED_DESCRIPTION = "This command displays all bgUsers that wasn't registered yet";

    @Autowired
    UserService userService;
    @Autowired
    BGUserService bgUserService;
    @Autowired
    XLSXReportValidationService xlsxReportValidationService;

    public ValidationReportCommand() {
        super(COMMAND_IDENTIFIER, COMMAND_DESCRIPTION);
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        Long userId = Long.valueOf(user.getId());
        log.info("BG report requested");
        SendMessage replyMessage = new SendMessage();
        replyMessage.setChatId(chat.getId().toString());
        replyMessage.enableHtml(true);
        if (userService.getUserByTelegramId(userId).isAdmin()) {
            List<BGUser> bgUsersList = bgUserService.getListOfUnregistratedBGUsers();
            if (!bgUsersList.isEmpty()) {
             SendDocument sendDocument = xlsxReportValidationService.writeReportBGUserNotRegitred(bgUsersList);
             sendDocument.setChatId(String.valueOf(chat.getId()));
             String reply = String.format("Не зарегистрировано %d пользователей", bgUsersList.size());
                replyMessage.setText(reply);
                execute(absSender,replyMessage,user);
                execute(absSender,sendDocument,user);
                log.info("Unregistred users in report " +bgUsersList.size());
            } else {
                String reply ="Все пользователи зарегистрированы)";
                replyMessage.setText(reply);
                execute(absSender,replyMessage,user);
                log.info("Registered all users");
            }
        } else {
            replyMessage.setText("Вы не обладаете соответствующим уровнем доступа.");
            execute(absSender, replyMessage, user);
            log.info("Access denied to BG report command for user wit id: " + userId);
        }
    }

    void execute(AbsSender sender, SendMessage message, User user) {
        try {
            sender.execute(message);
        } catch (TelegramApiException e) {
        }
    }
    void execute(AbsSender sender, SendDocument sendDocument, User user) {
        try {
            sender.execute(sendDocument);
        } catch (TelegramApiException e) {
        }
    }

}
