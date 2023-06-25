package ru.kumkuat.application.gameModule.marshakCommands;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.kumkuat.application.gameModule.models.TelegramChat;
import ru.kumkuat.application.gameModule.service.SceneService;
import ru.kumkuat.application.gameModule.service.TelegramChatService;
import ru.kumkuat.application.gameModule.service.UserService;

import java.util.Objects;

@Slf4j
@Service
public class KickCommand extends BotCommand implements AdminCommand {

    @Autowired
    private UserService userService;
    @Autowired
    private TelegramChatService telegramChatService;
    @Autowired
    private SceneService sceneService;

    public KickCommand() {
        super("/kick", "Kick user from playing chats\n");
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        Long userId = Long.valueOf(user.getId());
        if (userService.getUserByTelegramId(userId).isAdmin()) {
            Long kickUserId = -1L;
            if (arguments != null && arguments.length > 0) {
                try {
                    kickUserId = Long.parseLong(arguments[0]);
                } catch (NumberFormatException e) {
                    log.info("wrong number format when you kick him");
                }
            }
            KickChatMember(absSender, kickUserId);
        } else {
            SendMessage replyMessage = new SendMessage();
            replyMessage.setChatId(chat.getId().toString());
            replyMessage.enableHtml(true);
            replyMessage.setText("Вы не обладаете соответствующим уровнем доступа.");
            execute(absSender, replyMessage, user);
            log.info("Access denied to Kick command for user wit id: {}", userId);
        }
    }

    public void KickChatMember(AbsSender absSender, Long userId) {
        var busyChatsList = telegramChatService.getBusyChats();
        for (var busyChat :
                busyChatsList) {
            if (Objects.equals(busyChat.getUserId(), userId) || userId < 0) {
                KickChatMember(absSender, busyChat);
                log.info("User with id: {} was kicked from chat", userId);
            }
        }
    }

    public void KickAllChatMember(AbsSender absSender) {
        KickChatMember(absSender, -1l);
    }

    private boolean KickChatMember(AbsSender absSender, TelegramChat busyChat) {
//        KickChatMember kickChatMember = new KickChatMember();
//        Long userId = busyChat.getUserId();
//        kickChatMember.setChatId(busyChat.getChatId().toString());
//        kickChatMember.setUserId(userId.intValue());
//        Duration duration = Duration.ofSeconds(30);
//        kickChatMember.forTimePeriodDuration(duration);
//        try {
//            if (absSender.execute(kickChatMember)) {
//                ExportChatInviteLink exportChatInviteLink = new ExportChatInviteLink(busyChat.getChatId().toString());
//                StringBuilder finalMessage = new StringBuilder();
//                finalMessage.append(absSender.execute(exportChatInviteLink));
//
//                procceseUser(absSender, userId, finalMessage);
//                cleanChat(busyChat);
//
//                sendNotificationToAdminChat(absSender, finalMessage.toString());
//                log.info("User with id: {} was kicked from chat", userId);
//                return true;
//            } else {
//                return false;
//            }
//        } catch (TelegramApiException e) {
//            log.info("exception in kickCommand execution", e);
            return false;
//        }
    }

    private void sendNotificationToAdminChat(AbsSender absSender, String notificationText) throws TelegramApiException {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(notificationText);
        sendMessage.setChatId(telegramChatService.getAdminChatId());
        sendMessage.enableHtml(true);
        absSender.execute(sendMessage);
    }

    private void procceseUser(AbsSender absSender, Long userId, StringBuilder message) {
        var player = userService.getUserByTelegramId(userId);
        userService.setPlaying(player.getTelegramUserId(), false); /* Make isPlaying = false */

        String name = player.getName() != null ? player.getName() : "без имени";
        message.append(String.format("Пользователь: @ %s id: %s успешно удален из чата", name, userId));
        if (userService.getUserByTelegramId(userId).getSceneId() >= sceneService.count()) {
            userService.setUserPayment(userId, false);  /* Drop isPay only if User reached the end of the Game   */
            userService.setUserScene(userId, 0);
            log.info("User {} finished game", userId);
        } else {
            notifyUser(absSender, userId);
        }

    }

    private void notifyUser(AbsSender absSender, Long userId) {
        try {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setText( "Вы были выпровожены из чата. Но так как вы не дошли до конца,\n" +
                    "то можете продолжить в любой момент просто нажав \"Начать Прогулку\".");
            sendMessage.setChatId(userId.toString());
            sendMessage.enableHtml(true);
            absSender.execute(sendMessage);
            log.info("User {} was notified", userId);
        } catch (TelegramApiException ee) {
            log.warn("Notifaction of user faild", ee);
        }

    }

    private void cleanChat(TelegramChat chat) {
        long chatId = chat.getChatId();
        chat.setBusy(false);
        chat.setStartPlayTime(null);
        chat.setUserId(null);
        telegramChatService.saveChatIntoDB(chat);
        log.info("Chat {} was cleaned", chatId);
    }

    private void execute(AbsSender sender, SendMessage message, User user) {
        try {
            sender.execute(message);
        } catch (TelegramApiException e) {
            log.info("exception in kickCommand execution");
        }
    }

}
