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
import ru.kumkuat.application.GameModule.Repository.UserRepository;
import ru.kumkuat.application.GameModule.Abstract.TelegramWebhookCommandBot;
import ru.kumkuat.application.GameModule.Service.TelegramChatService;
import ru.kumkuat.application.GameModule.Service.TimerService;
import ru.kumkuat.application.GameModule.Service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

@Slf4j
@Service
public class ResetUserCommand extends BotCommand implements IListenerSupport {

    private final List<TelegramWebhookCommandBot> listeners = new ArrayList<>();
    private final UserService userService;
    @Autowired
    private TelegramChatService telegramChatService;

    public ResetUserCommand(UserService userService) {
        super("/reset_user", "Перезагрузить пользователя");
        this.userService = userService;
    }

    private long userId;

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {

        if (arguments != null && arguments.length > 0) {
            log.debug("Marshak");
            try {
                userId = Long.valueOf(arguments[0]);
                if (/*userService.IsUserExist(userId) && */telegramChatService.isUserAlreadyGetChat(userId)) {
                    try {
                        var player = userService.getUserByTelegramId(userId);
                        //kickAllCommand.KickChatMember(marshakBot, userId);
                        InvokeKickCommand(user, chat);
                        player.setTriggered(false);
                        userService.saveUser(player);

                        Timer timer = new Timer(true);
                        TimerService timerService = new TimerService();
                        timerService.setTimerOperation(() -> TimerOperation(user, chat));
                        timer.schedule(timerService, 120 * 100);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void TimerOperation(User user, Chat chat) {
        try {
            //sendChatCommand.SendFreeChat(marshakBot, userId);
            InvokeSendFreeChatCommand(user, chat);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void execute(AbsSender sender, SendMessage message, User user) {
        try {
            sender.execute(message);
        } catch (TelegramApiException e) {
        }
    }

    @Override
    public void addListener(TelegramWebhookCommandBot telegramWebhookCommandBot) {
        listeners.add(telegramWebhookCommandBot);
    }

    private void InvokeSendFreeChatCommand(User user, Chat chat) {
        var arguments = new String[]{String.valueOf(userId)};
        for (var bot :
                listeners) {
            bot.InvokeCommand("sendchat", user, chat, arguments);
        }
    }

    private void InvokeKickCommand(User user, Chat chat) {
        var arguments = new String[]{String.valueOf(userId)};
        for (var bot :
                listeners) {
            bot.InvokeCommand("kick", user, chat, arguments);
        }
    }
}
