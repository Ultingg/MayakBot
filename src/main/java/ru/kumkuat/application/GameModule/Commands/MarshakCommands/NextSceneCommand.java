package ru.kumkuat.application.GameModule.Commands.MarshakCommands;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.kumkuat.application.GameModule.Service.SceneService;
import ru.kumkuat.application.GameModule.Service.UserService;

@Component
public class NextSceneCommand extends BotCommand {

    private static final String COMMAND_DESCRIPTION = "Go to next scene!. Use /next [command] for more info";
    private static final String EXTENDED_DESCRIPTION = "This command displays all commands the bot has to offer.\n /help [command] can display deeper information";


    private final UserService userService;
    private final SceneService sceneService;

    public NextSceneCommand(UserService userService, SceneService sceneService) {
        super("/next", COMMAND_DESCRIPTION);
        this.userService = userService;
        this.sceneService = sceneService;
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        SendMessage replyMessage = new SendMessage();
        replyMessage.setChatId(chat.getId().toString());
        replyMessage.enableHtml(true);
        Long userId = Long.valueOf(user.getId());
        if (userService.IsUserExist(userId) && userService.getUser(userId).isAdmin()) {
            Integer oldSceneId = Math.toIntExact(userService.getUser(userId).getSceneId());
            Integer newSceneId = oldSceneId + 1;
            if (newSceneId < sceneService.count() - 1) {
                userService.setUserScene(user, newSceneId);
                replyMessage.setText("Вы продвинулись на сцену вперед! Номер сцены: " + newSceneId);
            } else {
                userService.setUserScene(user, 0);
                replyMessage.setText("Вы зашли слишком далеко, начинайте заново!");
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
        }
    }
}
