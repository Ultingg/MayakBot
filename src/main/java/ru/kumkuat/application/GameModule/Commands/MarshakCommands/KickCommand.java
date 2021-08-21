package ru.kumkuat.application.GameModule.Commands.MarshakCommands;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.groupadministration.ExportChatInviteLink;
import org.telegram.telegrambots.meta.api.methods.groupadministration.KickChatMember;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.kumkuat.application.GameModule.Models.TelegramChat;
import ru.kumkuat.application.GameModule.Service.TelegramChatService;
import ru.kumkuat.application.GameModule.Service.UserService;

import java.time.Duration;
import java.util.Objects;
@Slf4j
@Service
public class KickCommand extends BotCommand implements AdminCommand {
    @Autowired
    private UserService userService;
    @Autowired
    private TelegramChatService telegramChatService;

    public KickCommand() {
        super("/kick", "Kick user from playing chats\n");
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        Long userId = Long.valueOf(user.getId());
        if (userService.getUserByTelegramId(userId).isAdmin()) {
            Long kickUserId = -1l;
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
            log.info("Access denied to Kick command for user wit id: " + userId);
        }
    }

    public void KickChatMember(AbsSender absSender, Long userId) {
        var busyChatsList = telegramChatService.getBusyChats();
        for (var busyChat :
                busyChatsList) {
            if (Objects.equals(busyChat.getUserId(), userId) || userId < 0) {
                KickChatMember(absSender, busyChat);
                log.info("User with id:" + userId + " was kicked from chat");
            }
        }
    }

    public void KickAllChatMember(AbsSender absSender) {
        KickChatMember(absSender, -1l);
    }

    private boolean KickChatMember(AbsSender absSender, TelegramChat busyChat) {
        KickChatMember kickChatMember = new KickChatMember();
        Long userId = busyChat.getUserId();
        kickChatMember.setChatId(busyChat.getChatId().toString());
        kickChatMember.setUserId(userId.intValue());
        Duration duration = Duration.ofSeconds(30);
        kickChatMember.forTimePeriodDuration(duration);
        try {
            busyChat.setBusy(false);
            busyChat.setUserId(null);
            busyChat.setStartPlayTime(null);
            if (userService.getUserByTelegramId(userId).getSceneId() < 19) {
                userService.setUserPayment(userId, false);  /** Drop isPay only if User reached the end of the Game   */
            }
            telegramChatService.saveChatIntoDB(busyChat);
            if (absSender.execute(kickChatMember)) {
                ExportChatInviteLink exportChatInviteLink = new ExportChatInviteLink(busyChat.getChatId().toString());
                SendMessage sendMessage = new SendMessage();
                sendMessage.setText(absSender.execute(exportChatInviteLink));
                sendMessage.setChatId(telegramChatService.getAdminChatId());
                sendMessage.enableHtml(true);
                absSender.execute(sendMessage);

                var player = userService.getUserByTelegramId(busyChat.getUserId());
                var name = player.getTelegramUserId();
                userService.setPlaying(Long.valueOf(player.getTelegramUserId()), false); /** Make isPlaying = false */
                sendMessage.setText("Пользователь: @" + name + " успешно удален из чата");
                sendMessage.setChatId(telegramChatService.getAdminChatId());
                sendMessage.enableHtml(true);
                log.info("User with id:" + userId + " was kicked from chat");
                return true;
            } else {
                return false;
            }

        } catch (TelegramApiException e) {
            e.printStackTrace();
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    void execute(AbsSender sender, SendMessage message, User user) {
        try {
            sender.execute(message);
        } catch (TelegramApiException e) {
        }
    }

}
