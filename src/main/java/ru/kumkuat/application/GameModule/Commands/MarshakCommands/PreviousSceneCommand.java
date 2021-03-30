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
public class PreviousSceneCommand extends BotCommand {


    private final UserService userService;
    private final SceneService sceneService;

    public PreviousSceneCommand(UserService userService, SceneService sceneService) {
        super("/previous", "Back to previous scene!\n");
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
            Integer newSceneId = oldSceneId - 1;
            if (newSceneId > 0) {
                userService.setUserScene(user, newSceneId);
                replyMessage.setText("Вы вернулись на предыдущую сцену! Номер сцены: " + newSceneId);
            } else {
                userService.setUserScene(user, 0);
                replyMessage.setText("Вот вы и вернулись в начало, всегда возвращайтесь к Началу!");
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
