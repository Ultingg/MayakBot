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
public class ResetCommand extends BotCommand {
    @Autowired
    private UserService userService;

    public ResetCommand() {
        super("/reset", "Reset your scene count!\n");
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        SendMessage replyMessage = new SendMessage();
        replyMessage.setChatId(chat.getId().toString());
        replyMessage.enableHtml(true);

        if(userService.IsUserExist(user.getId().longValue())){
                userService.setUserScene(user, 0);
                replyMessage.setText("Ваш игровой прогресс успешно сброшен");

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
