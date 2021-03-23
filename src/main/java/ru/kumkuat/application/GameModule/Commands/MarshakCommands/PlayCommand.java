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
        replyMessage.enableHtml(true);


        if(user.getUserName() == null){
            replyMessage.setText("Ты человек без имени. С тобой играть не получится. Разберись в себе для начала...");
        }
        else if(user.getUserName().equals("GroupAnonymousBot")){
            replyMessage.setText("Нужно выключить ананонимность. Ты не бэтмэн! Сними маску -_-");
        }
        else if (!userService.IsUserExist(user.getUserName())) {

            try {
                userService.setUserIntoDB(user);
            } catch (Exception e) {
                e.printStackTrace();
            }
            replyMessage.setText("Доступ к игровому сценарию успешно предоставлен!");
        } else {
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
