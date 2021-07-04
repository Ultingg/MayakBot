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
public class ResetUserCommand extends BotCommand {

    private final UserService userService;
    @Autowired
    private TelegramChatService telegramChatService;
    @Autowired
    private MarshakBot marshakBot;
    @Autowired
    private SendChatCommand sendChatCommand;
    @Autowired
    private KickCommand kickAllCommand;
    @Autowired
    private UserRepository userRepository;

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
                if (userService.IsUserExist(userId) && telegramChatService.isUserAlreadyPlaying(userId)) {
                    try {
                        var player = userService.getUser(userId);
                        kickAllCommand.KickChatMember(marshakBot, userId);
                        player.setTriggered(false);
                        userRepository.save(player);

                        Timer timer = new Timer(true);
                        TimerService timerService = new TimerService();
                        timerService.setTimerOperation(() -> TimerOperation());
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

    public void TimerOperation() {
        try {
            sendChatCommand.SendFreeChat(marshakBot, userId);
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
}
