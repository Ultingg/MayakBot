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
import ru.kumkuat.application.GameModule.Service.TelegramChatService;
import ru.kumkuat.application.GameModule.Service.UserService;

@Slf4j
@Service
public class StartCommand extends BotCommand {

    private final UserService userService;
    @Autowired
    private TelegramChatService telegramChatService;

    @Autowired
    private HelpCommand helpCommand;

    public StartCommand(UserService userService) {
        super("/start", "to start!\n");
        this.userService = userService;
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        if (user.getId().longValue() == chat.getId()) {

            log.debug("Marshak ");

            /*if (user.getUserName() == null) {
                //replyMessage.setText("Ты человек без имени. С тобой играть не получится. Разберись в себе для начала...");
            } else if (user.getUserName().equals("GroupAnonymousBot")) {
                //replyMessage.setText("Нужно выключить ананонимность. Ты не бэтмэн! Сними маску -_-");
            } else */
            if (!userService.IsUserExist(user.getId().longValue())) {
                try {
                    userService.setUserIntoDB(user);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //replyMessage.setText("Вы успешно зарегистрировались!");
            }

            SendMessage replyMessage = new SendMessage();
            replyMessage.setChatId(chat.getId().toString());
            replyMessage.enableHtml(true);
            replyMessage.setText("Привет! Мы рады приветствовать тебя!");
            execute(absSender, replyMessage, user);

            helpCommand.execute(absSender, user, chat, arguments);
        }
    }

    void execute(AbsSender sender, SendMessage message, User user) {
        try {
            sender.execute(message);
        } catch (TelegramApiException e) {
        }
    }
}
