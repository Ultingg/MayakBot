package ru.kumkuat.application.GameModule.Commands.MarshakCommands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChat;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.kumkuat.application.GameModule.Exceptions.TelegramChatServiceException;
import ru.kumkuat.application.GameModule.Service.TelegramChatService;
import ru.kumkuat.application.GameModule.Service.UserService;

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

        if (userService.IsUserExist(user.getId().longValue())) {
            try {
                GetChat getChat = new GetChat();
                getChat.setChatId(chat.getId().toString());


                try {
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
                } catch (TelegramApiException ex) {
                    replyMessage.setText("Чат не добавлен, возникли неполадки.");
                }
            } catch (Exception e) {
                e.printStackTrace();
                replyMessage.setText("Чат не добавлен, возникли неполадки.");
            }
        } else {
            replyMessage.setText("Вам надо сначала зарегистрироваться.");
        }

        execute(absSender, replyMessage, user);
    }

    void execute(AbsSender sender, SendMessage message, User user) {
        try {
            sender.execute(message);
        } catch (TelegramApiException e) {
        }
    }
}
