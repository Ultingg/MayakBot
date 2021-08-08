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
import ru.kumkuat.application.GameModule.Abstract.TelegramWebhookCommandBot;
import ru.kumkuat.application.GameModule.Bot.MarshakBot;
import ru.kumkuat.application.GameModule.Service.BGUserService;
import ru.kumkuat.application.GameModule.Service.UserService;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class PlayMarathonCommand extends BotCommand implements IListenerSupport {

    private final List<TelegramWebhookCommandBot> listeners = new ArrayList<>();
    private final UserService userService;
    private final BGUserService bgUserService;
    @Autowired
    private MarshakBot marshakBot;


    public PlayMarathonCommand(UserService userService, BGUserService bgUserService) {
        super("/play_marathon", "После этой команды начнется марафон");
        this.userService = userService;
        this.bgUserService = bgUserService;
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        long userId = user.getId().longValue();

        if (userId == marshakBot.getId() && userService.IsUserExist(chat.getId())) {
            userId = chat.getId();

        }

        if (userId == chat.getId()) {
            String username = chat.getUserName();

            log.debug("Marshak ");
            SendMessage replyMessage = new SendMessage();
            replyMessage.setChatId(chat.getId().toString());
            replyMessage.enableHtml(true);

            try {
                if (!userService.IsUserExist(userId)) {
                    try {
                        userService.setUserIntoDB(user); // а зачем это здесь? user - же это бот в данном случае
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                if (userService.validateUsersAndBGUsers(username)) {
                    if (bgUserService.isItTimeToStart(username)) {
                        userService.setUserPayment(userId, true);
                        InvokePlayCommand(user, chat, arguments);
                    } else {
                        String message = bgUserService.getTimeStartMessageForUser(username);
                        replyMessage.setText(message);
                        execute(absSender, replyMessage, user);
                    }

                } else {
                    replyMessage.setText("Если вы еще не приобретали билет на показ 31 июля, " +
                            "тогда мы в ближайшее время настроим вам время начала спектакля — " +
                            "в соответствии с указанными в анкете пожеланиями.");
                    execute(absSender, replyMessage, user);
                    replyMessage.setText("Приобрести билеты на 31.07 можно тут:");
                    execute(absSender, replyMessage, user);
                    replyMessage.setText("https://runcity.timepad.ru/event/1700782/#register");
                    execute(absSender, replyMessage, user);
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

    private boolean isUserIsBGUser(User user) {
        return bgUserService.isBGUserExistByUsername(user.getUserName());
    }

    @Override
    public void addListener(TelegramWebhookCommandBot telegramWebhookCommandBot) {
        listeners.add(telegramWebhookCommandBot);
    }
    private void InvokePlayCommand(User user, Chat chat, String[] arguments) {
        for (var bot :
                listeners) {
            bot.InvokeCommand("play", user, chat, arguments);
        }
    }
}
