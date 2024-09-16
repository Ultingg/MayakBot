package ru.kumkuat.application.gameModule.marshakCommands;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.kumkuat.application.gameModule.bot.MarshakBot;
import ru.kumkuat.application.gameModule.models.User;
import ru.kumkuat.application.gameModule.service.UserService;

@Service
public class CommandService {

    private final MarshakBot marshakBot;
    private final UserService userService;


    public CommandService(MarshakBot marshakBot, UserService userService) {
        this.marshakBot = marshakBot;
        this.userService = userService;
    }

    public boolean commandChecker(Message message) {
        return marshakBot.isCommand(message.getText());
    }


    public void resolveCommandMessage(Update update) {
        Message updateMessage = update.getMessage();
        User user = userService.getUserByTelegramId(updateMessage.getFrom().getId());
        userService.registerUser(updateMessage.getFrom());
        if (updateMessage.getChat().getType().equals("private") || (user != null && user.isAdmin())) {
            marshakBot.onWebhookUpdateReceived(update);
        }
    }
}


