package ru.kumkuat.application.gameModule.marshakCommands;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChat;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.kumkuat.application.gameModule.exceptions.TelegramChatServiceException;
import ru.kumkuat.application.gameModule.service.TelegramChatService;
import ru.kumkuat.application.gameModule.service.UserService;

@Slf4j
@Service
public class SaveChatCommand extends BotCommand implements AdminCommand {
    @Autowired
    private UserService userService;
    @Autowired
    private TelegramChatService telegramChatService;

    public SaveChatCommand() {
        super("/savechat", "Save chat into DB\n");
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        SendMessage replyMessage = new SendMessage();
        replyMessage.setChatId(chat.getId().toString());
        replyMessage.enableHtml(true);
        Long userId = Long.valueOf(user.getId());

        if (/*userService.IsUserExist(user.getId().longValue()) && */userService.getUserByTelegramId(userId).isAdmin()) {
            try {
                GetChat getChat = new GetChat();
                getChat.setChatId(chat.getId().toString());

                var chatInfo = absSender.execute(getChat);

                var tittle = chatInfo.getTitle();
                var inviteLink = chatInfo.getInviteLink();

                if (inviteLink != null && tittle != null) {
                    System.out.println(tittle);
                    System.out.println(inviteLink);
                    telegramChatService.setChatIntoDB(chatInfo);
                    replyMessage.setText("Ссылка записана: " + inviteLink);
                } else {
                    replyMessage.setText("Не удалось получить ссылку");
                }
            } catch (TelegramChatServiceException ex) {
                replyMessage.setText(ex.getMessage());
                log.error(ex.getMessage());
            } catch (TelegramApiException ex) {
                replyMessage.setText("Чат не добавлен, возникли неполадки.");
                log.error(ex.getMessage());
            } catch (Exception e) {
                log.error(e.getMessage());
                replyMessage.setText("Чат не добавлен, возникли неполадки.");
            }
        } else {
            replyMessage.setText("Вы не обладаете соответствующим уровнем доступа.");
        }

        execute(absSender, replyMessage, user);
    }

    void execute(AbsSender sender, SendMessage message, User user) {
        try {
            sender.execute(message);
        } catch (TelegramApiException e) {
            log.error("Error while executing SaveChatCommand", e);
        }
    }
}
