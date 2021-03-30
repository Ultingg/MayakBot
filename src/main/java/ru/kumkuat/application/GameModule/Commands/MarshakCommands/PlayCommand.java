package ru.kumkuat.application.GameModule.Commands.MarshakCommands;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.kumkuat.application.GameModule.Service.UserService;

@Slf4j
@Service
public class PlayCommand extends BotCommand {

    private final UserService userService;

    public PlayCommand(UserService userService) {
        super("/play", "Write that command and lets get to play!\n");
        this.userService = userService;
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        log.debug("Marshak ");
        SendMessage replyMessage = new SendMessage();
        replyMessage.setChatId(chat.getId().toString());
        replyMessage.enableHtml(true);
        Long userId = Long.valueOf(user.getId());

        if (user.getUserName().equals("GroupAnonymousBot")) {
            replyMessage.setText("Нужно выключить ананонимность. Ты не бэтмэн! Сними маску -_-");
        } else if (!userService.IsUserExist(userId)) {

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
