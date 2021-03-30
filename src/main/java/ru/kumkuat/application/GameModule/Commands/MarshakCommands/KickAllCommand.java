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
        super("/kickall", "Kick all into all playing chats\n");
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        KickChatMember kickChatMember = new KickChatMember();
        Long userId = Long.valueOf(user.getId());
        if (userService.getUser(userId).isAdmin()) {
            var busyChatsList = telegramChatService.getBusyChats();
            for (var busyChat :
                    busyChatsList) {
                kickChatMember.setChatId(busyChat.getChatId().toString());
                kickChatMember.setUserId(busyChat.getUserId().intValue());
                Duration duration = Duration.ofSeconds(60);
                kickChatMember.forTimePeriodDuration(duration);
                try {
                    var player = userService.getUser(busyChat.getUserId());

                    busyChat.setBusy(false);
                    busyChat.setUserId(null);
                    busyChat.setStartPlayTime(null);
                    telegramChatService.saveChatIntoDB(busyChat);

                    if (absSender.execute(kickChatMember)) {
                        SendMessage sendMessage = new SendMessage();

                        var name = player.getName();
                        if (name == null) {
                            name = "";
                            if (user.getLastName() != null) {
                                name += player.getLastName();
                            }
                            if (user.getFirstName() != null) {
                                name += player.getFirstName();
                            }
                        }
                        sendMessage.setText("Пользователь: @" + name + " успешно удален из чата");
                        sendMessage.setChatId(chat.getId().toString());
                        sendMessage.enableHtml(true);
                        absSender.execute(sendMessage);
                    }
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            for (var busyChat :
                    telegramChatService.getAll()) {
                ExportChatInviteLink exportChatInviteLink = new ExportChatInviteLink(busyChat.getChatId().toString());
                try {
                    var inviteLink = absSender.execute(exportChatInviteLink);
                    SendMessage sendMessage = new SendMessage();
                    sendMessage.setText(inviteLink);
                    sendMessage.setChatId(chat.getId().toString());
                    sendMessage.enableHtml(true);
                    absSender.execute(sendMessage);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
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

    void execute(AbsSender sender, SendMessage message, User user) {
        try {
            sender.execute(message);
        } catch (TelegramApiException e) {
        }
    }
}
