package ru.kumkuat.application.GameModule.Commands.MarshakCommands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.kumkuat.application.GameModule.Service.UserService;

@Service
public class PlayCommand extends BotCommand {
    @Autowired
    private UserService userService;
    public PlayCommand() {
        super("/play", "Write that command and lets get to play!\n");
    }
    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        SendMessage replyMessage = new SendMessage();
        replyMessage.setChatId(chat.getId().toString());
        if(!userService.IsUserExist(user.getUserName())){
            userService.setUserIntoDB(user);
            replyMessage.enableHtml(true);
            replyMessage.setText("Доступ к игровому сценарию успешно предоставлен!");
        }
        else{
            replyMessage.enableHtml(true);
            replyMessage.setText("Вам уже предоставлен доступ.");
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
