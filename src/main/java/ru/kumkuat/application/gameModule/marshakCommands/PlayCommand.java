package ru.kumkuat.application.gameModule.marshakCommands;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.kumkuat.application.gameModule.models.TelegramChat;
import ru.kumkuat.application.gameModule.service.SceneService;
import ru.kumkuat.application.gameModule.service.TelegramChatService;
import ru.kumkuat.application.gameModule.service.UserService;

@Slf4j
@Service
public class PlayCommand extends BotCommand {

    private final UserService userService;
    private final TelegramChatService telegramChatService;
    private final SceneService sceneService;
    private final SendChatCommand sendChatCommand;

    public PlayCommand(UserService userService, TelegramChatService telegramChatService, SceneService sceneService, SendChatCommand sendChatCommand) {
        super("/play", "После этой команды начнется игра");
        this.userService = userService;
        this.telegramChatService = telegramChatService;
        this.sceneService = sceneService;
        this.sendChatCommand = sendChatCommand;
    }

    @Override
    public void processMessage(AbsSender absSender, Message message, String[] arguments) {
        execute(absSender, message.getFrom(), message.getChat(), arguments);
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {

        long userId = chat.getId();
        log.debug("Marshak ");
        SendMessage replyMessage = new SendMessage();
        replyMessage.setChatId(chat.getId().toString());
        replyMessage.enableHtml(true);

        try {
            if (userService.IsUserHasPayment(userId)) {
                SendFreeChat(absSender, userId);
            } else {
                replyMessage.setText("Необходимо внести оплату!");
                execute(absSender, replyMessage, user);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * What will happend when user end the game and press Start Promenad, but didn't pay again?
     */
    void SendFreeChat(AbsSender absSender, Long userId) throws Exception {
        SendMessage replyMessage = new SendMessage();
        replyMessage.setChatId(userId.toString());
        replyMessage.enableHtml(true);
        if (userService.IsUserExist(userId.longValue())) {
            if (!telegramChatService.isUserAlreadyGetChat(userId)) {
                if (userService.getUserByTelegramId(userId).getSceneId() < 1) {
                    userService.setPlaying(Long.valueOf(userId), true);
                    replyMessage.setText("Отлично! Как я могу к тебе обращаться? ");
                    absSender.execute(replyMessage);
                } else if (userService.getUserByTelegramId(userId).getSceneId() == sceneService.count()) {
                    replyMessage.setText("Вы окончили прогулку, поздравляю! Чтобы начать прогулку заново обратитесь к администратору (/help)");
                    absSender.execute(replyMessage);
                } /* тут должна отрабатвать проверка на оплату,
                         чтобы сюда пользователь не попадал, а пока закроем дырку так.  */ else {
                    userService.setPlaying(Long.valueOf(userId), true);
                    replyMessage.setText("Понеслась душа в рай!");
                    sendChatCommand.sendFreeChat(absSender, userId);/** Sending Link to chat if User didn't finish the Game **/

                }
            } else if (telegramChatService.isUserAlreadyGetChat(userId)) {
                TelegramChat freeChat = telegramChatService.getChatByUserTelegramId(userId);
                log.info("User's been invited to chat {}, resending invite link..", freeChat.getChatId());
                sendChatCommand.sendLinkToChat(replyMessage, absSender, freeChat, userId);
            } else {
                replyMessage.setText("Вы уже начали игру. Чтобы начать заново, нужно ее закончить.");
                absSender.execute(replyMessage);
            }
        }
    }

    void execute(AbsSender sender, SendMessage message, User user) {
        try {
            sender.execute(message);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }
}
