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
import ru.kumkuat.application.GameModule.Service.UserService;

@Service
public class SetChatCommand extends BotCommand {
    @Autowired
    private UserService userService;

    public SetChatCommand() {
        super("/setchat", "Save chat into DB\n");
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
//        SendMessage replyMessage = new SendMessage();
//        replyMessage.setChatId(chat.getId().toString());
//        replyMessage.enableHtml(true);

        GetChat getChat = new GetChat();
        getChat.setChatId(chat.getId().toString());
        Long userId = Long.valueOf(user.getId());
        if (userService.IsUserExist(userId)) {
            try {

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            //replyMessage.setText("Вам надо сначала зарегистрироваться.");
        }
        execute(absSender, getChat);
    }

    void execute(AbsSender sender, SendMessage message, User user) {
        try {
            sender.execute(message);
        } catch (TelegramApiException e) {
        }
    }
    void execute(AbsSender sender, GetChat getChat) {
        try {
            sender.execute(getChat);
        } catch (TelegramApiException e) {
        }
    }
}
