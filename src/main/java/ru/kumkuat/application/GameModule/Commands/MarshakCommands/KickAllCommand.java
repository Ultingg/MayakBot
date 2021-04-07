package ru.kumkuat.application.GameModule.Commands.MarshakCommands;

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

@Service
public class KickAllCommand extends BotCommand implements AdminCommand {
    @Autowired
    private UserService userService;
    @Autowired
    private TelegramChatService telegramChatService;

    public KickAllCommand() {
        super("/kickall", "Kick all from all playing chats\n");
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {

        Long userId = Long.valueOf(user.getId());
        if (userService.getUser(userId).isAdmin()) {
            var busyChatsList = telegramChatService.getBusyChats();
            for (var busyChat :
                    busyChatsList) {
                var player = userService.getUser(busyChat.getUserId());
                if (kickChatMember(absSender, busyChat)) {
                    SendMessage sendMessage = new SendMessage();

                    var name = player.getName();
                    if (name == null) {
                        name = "";
                        if (player.getLastName() != null) {
                            name += player.getLastName();
                        }
                        if (player.getFirstName() != null) {
                            name += player.getFirstName();
                        }
                    }
                    sendMessage.setText("Пользователь: @" + name + " успешно удален из чата");
                    sendMessage.setChatId(chat.getId().toString());
                    sendMessage.enableHtml(true);
                    execute(absSender, sendMessage, user);
                }
            }
        } else {
            SendMessage replyMessage = new SendMessage();
            replyMessage.setChatId(chat.getId().toString());
            replyMessage.enableHtml(true);
            replyMessage.setText("Вы не обладаете соответствующим уровнем доступа.");
            execute(absSender, replyMessage, user);
        }

    }

    public boolean kickChatMember(AbsSender absSender, TelegramChat busyChat) {
        KickChatMember kickChatMember = new KickChatMember();
        Long userId = busyChat.getUserId();
        kickChatMember.setChatId(busyChat.getChatId().toString());
        kickChatMember.setUserId(userId.intValue());
        Duration duration = Duration.ofSeconds(600);
        kickChatMember.forTimePeriodDuration(duration);
        try {
            busyChat.setBusy(false);
            busyChat.setUserId(null);
            busyChat.setStartPlayTime(null);
            userService.setUserPayment(userId, false);
            telegramChatService.saveChatIntoDB(busyChat);
            if(absSender.execute(kickChatMember)){
                ExportChatInviteLink exportChatInviteLink = new ExportChatInviteLink(busyChat.getChatId().toString());
                SendMessage sendMessage = new SendMessage();
                sendMessage.setText(absSender.execute(exportChatInviteLink));
                sendMessage.setChatId(telegramChatService.getAdminChatId());
                sendMessage.enableHtml(true);
                absSender.execute(sendMessage);
                return true;
            }
            else {
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
