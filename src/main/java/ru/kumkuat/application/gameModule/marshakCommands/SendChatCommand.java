package ru.kumkuat.application.gameModule.marshakCommands;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.groupadministration.ExportChatInviteLink;
import org.telegram.telegrambots.meta.api.methods.groupadministration.UnbanChatMember;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import ru.kumkuat.application.gameModule.bot.MarshakBot;
import ru.kumkuat.application.gameModule.models.TelegramChat;
import ru.kumkuat.application.gameModule.service.TelegramChatService;
import ru.kumkuat.application.gameModule.service.UserService;

import java.util.Date;

@Slf4j
@Service
@PropertySource(value = "file:../resources/externalsecret.yml")
public class SendChatCommand extends BotCommand {


    @Value("${admin.chat.id}")
    private String adminChatId;

    @Autowired
    private UserService userService;
    @Autowired
    private TelegramChatService telegramChatService;
    @Autowired
    private MarshakBot marshakBot;


    public SendChatCommand() {
        super("/sendchat", "Направить пользователю ссылку на свободный чат");
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {

        long userId = user.getId();
        if (arguments != null && arguments.length > 0) {
            try {
                userId = Long.parseLong(arguments[0]);
            } catch (NumberFormatException e) {
                log.error("!!!!!!!!!Argument of command wasn't a number.!!!!!!!!! ", e);
            }
        }

        log.debug("Marshak ");
        SendMessage replyMessage = new SendMessage();
        replyMessage.setChatId(chat.getId().toString());
        replyMessage.enableHtml(true);

        try {
            try {
                sendFreeChat(absSender, userId);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void sendFreeChat(AbsSender absSender, Long userId) throws Exception {
        SendMessage replyMessage = new SendMessage();
        replyMessage.setChatId(userId.toString());
        replyMessage.enableHtml(true);
        if (telegramChatService.isFreeChatHas()) {
            var freeChat = telegramChatService.getFreeChat();
            freeChat.setBusy(true);
            freeChat.setUserId(userId);
            freeChat.setStartPlayTime(new Date());
            telegramChatService.saveChatIntoDB(freeChat);
            try {
                UnbanChatMember unbanChatMember = new UnbanChatMember();
                unbanChatMember.setChatId(freeChat.getChatId().toString());
                unbanChatMember.setUserId(userId);
                absSender.execute(unbanChatMember);
            } catch (TelegramApiRequestException ex) {
                log.error("User {} is owner of chat {} and can't be kicked", userId, freeChat.getChatId());
            }

            sendLinkToChat(replyMessage, absSender, freeChat, userId);
            //нужно сделать проверку что пользователь играет в беседке, которая зарезирвирована. Что он вошел в беседку.

        } else {
            replyMessage.setText("Нет свободных чатов, попробуйте позже");
            absSender.execute(replyMessage);
        }
    }



    public void sendLinkToChat(SendMessage replyMessage, AbsSender absSender, TelegramChat freeChat, Long userId) throws TelegramApiException {
        ExportChatInviteLink exportChatInviteLink = new ExportChatInviteLink(freeChat.getChatId().toString());
        var inviteLink = absSender.execute(exportChatInviteLink);
        log.info("Invite link to chat id: {} was sended to user: {}", freeChat.getChatId(), userId);

        ru.kumkuat.application.gameModule.models.User user = userService.getUserByTelegramId(userId);
        if (user != null && user.isTriggered()) {
            user.setTriggered(false);
            userService.save(user);
        }

        replyMessage.setText("Присоединяйся! Для старта напиши \"Привет\"");
        absSender.execute(replyMessage);
        replyMessage.setText(inviteLink);
        absSender.execute(replyMessage);

        try {
            sentChatLinkToAdminChat(replyMessage,absSender,user);
        } catch (Exception e){
            log.error("Exception happened while sending message with link to adminChat.");
        }
    }

    private void sentChatLinkToAdminChat(SendMessage replyMessage,AbsSender absSender, ru.kumkuat.application.gameModule.models.User user) throws TelegramApiException {
        String inviteLink = replyMessage.getText();
        String userName = user.getName();
        Long telegramId = user.getTelegramUserId();
        StringBuilder text = new StringBuilder();
        text.append("Пользавтель с telegramId: ")
                .append(telegramId)
                .append(" и name: ")
                .append(userName)
                .append(" отправлен в чат поссылке: ")
                .append(inviteLink);
        replyMessage.setText(text.toString());
        replyMessage.setChatId(adminChatId);
        replyMessage.enableHtml(true);
        absSender.execute(replyMessage);
    }
    void execute(AbsSender sender, SendMessage message, User user) {
        try {
            sender.execute(message);
        } catch (TelegramApiException e) {
        }
    }
}
