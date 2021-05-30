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
import ru.kumkuat.application.GameModule.Bot.MarshakBot;
import ru.kumkuat.application.GameModule.Repository.UserRepository;
import ru.kumkuat.application.GameModule.Service.TelegramChatService;
import ru.kumkuat.application.GameModule.Service.TimerService;
import ru.kumkuat.application.GameModule.Service.UserService;

import java.util.Timer;

@Slf4j
@Service
public class SetSceneNumberCommand extends BotCommand {

    private final UserService userService;
    @Autowired
    private TelegramChatService telegramChatService;
    @Autowired
    private UserRepository userRepository;

    public SetSceneNumberCommand(UserService userService) {
        super("/set_scene_number", "Перезагрузить пользователя");
        this.userService = userService;
    }

    private long userId;
    private long sceneId;

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {

        if (arguments != null && arguments.length > 1) {
            log.debug("Marshak");
            try {
                userId = Long.valueOf(arguments[0]);
                sceneId = Long.valueOf(arguments[1]);
                if (userService.IsUserExist(userId) && telegramChatService.isUserAlreadyPlaying(userId)) {
                    try {
                        var player = userService.getUser(userId);
                        player.setSceneId(sceneId);
                        player.setTriggered(false);
                        userRepository.save(player);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
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
