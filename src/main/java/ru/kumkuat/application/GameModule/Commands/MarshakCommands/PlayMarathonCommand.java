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
import ru.kumkuat.application.GameModule.Bot.*;
import ru.kumkuat.application.GameModule.Service.SceneService;
import ru.kumkuat.application.GameModule.Service.TelegramChatService;
import ru.kumkuat.application.GameModule.Service.UserService;

@Slf4j
@Service
public class PlayMarathonCommand extends BotCommand {

    private final UserService userService;
    @Autowired
    private MarshakBot marshakBot;

    public PlayMarathonCommand(UserService userService) {
        super("/play_marathon", "После этой команды начнется марафон");
        this.userService = userService;
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        long userId = user.getId().longValue();
        if (userId == marshakBot.getId() && userService.IsUserExist(chat.getId())) {
            userId = chat.getId();
        }

        if (userId == chat.getId()) {

            log.debug("Marshak ");
            SendMessage replyMessage = new SendMessage();
            replyMessage.setChatId(chat.getId().toString());
            replyMessage.enableHtml(true);

            try {
                if (!userService.IsUserExist(userId)) {
                    try {
                        userService.setUserIntoDB(user);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                replyMessage.setText("Если вы приобретали билет на показ 31 июля, " +
                        "в ближайшее время мы настроим вам время начала спектакля — " +
                        "в соответствии с указанными в анкете пожеланиями.");
                execute(absSender, replyMessage, user);
                replyMessage.setText("Приобрести билеты на 31.07 можно тут:");
                execute(absSender, replyMessage, user);
                replyMessage.setText("https://runcity.timepad.ru/event/1700782/#register");
                execute(absSender, replyMessage, user);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    void execute(AbsSender sender, SendMessage message, User user) {
        try {
            sender.execute(message);
        } catch (TelegramApiException e) {
        }
    }
}
