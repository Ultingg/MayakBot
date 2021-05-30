package ru.kumkuat.application.GameModule.Commands.MarshakCommands;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.groupadministration.ExportChatInviteLink;
import org.telegram.telegrambots.meta.api.methods.groupadministration.UnbanChatMember;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.kumkuat.application.GameModule.Bot.MarshakBot;
import ru.kumkuat.application.GameModule.Repository.UserRepository;
import ru.kumkuat.application.GameModule.Service.TelegramChatService;
import ru.kumkuat.application.GameModule.Service.UserService;

@Slf4j
@Service
public class SendChatCommand extends BotCommand {

    private final UserService userService;
    @Autowired
    private TelegramChatService telegramChatService;
    @Autowired
    private MarshakBot marshakBot;


    public SendChatCommand(UserService userService) {
        super("/sendchat", "Направить пользователю ссылку на свободный чат");
        this.userService = userService;
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {

        long userId = user.getId().longValue();
        if (userId == marshakBot.getId() && userService.IsUserExist(chat.getId())) {
            userId = chat.getId();
        }

        if (userId == chat.getId()) {

            if(arguments != null && arguments.length > 0){
                try {
                    userId = Long.valueOf(arguments[0]);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }

            log.debug("Marshak ");
            SendMessage replyMessage = new SendMessage();
            replyMessage.setChatId(chat.getId().toString());
            replyMessage.enableHtml(true);

            try {
                if (userService.IsUserExist(userId) ) {
                    try {
                        SendFreeChat(absSender, Long.valueOf(userId));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    void SendFreeChat(AbsSender absSender, Long userId) throws Exception {
        SendMessage replyMessage = new SendMessage();
        replyMessage.setChatId(userId.toString());
        replyMessage.enableHtml(true);
        if (telegramChatService.isFreeChatHas()) {
            var freeChat = telegramChatService.getFreeChat();
            freeChat.setBusy(true);
            //freeChat.setStartPlayTime(new Date());
            freeChat.setUserId(userId.longValue());
            telegramChatService.saveChatIntoDB(freeChat);

            UnbanChatMember unbanChatMember = new UnbanChatMember();
            unbanChatMember.setChatId(freeChat.getChatId().toString());
            unbanChatMember.setUserId(userId.intValue());
            absSender.execute(unbanChatMember);

            ExportChatInviteLink exportChatInviteLink = new ExportChatInviteLink(freeChat.getChatId().toString());
            var inviteLink = absSender.execute(exportChatInviteLink);

            replyMessage.setText("Присоединяйся! Для старта напиши \"Привет\"");
            absSender.execute(replyMessage);
            replyMessage.setText(inviteLink);
            absSender.execute(replyMessage);
            //нужно сделать проверку что пользователь играет в беседке, которая зарезирвирована. Что он вошел в беседку.

        } else {
            replyMessage.setText("Нет свободных чатов, попробуйте позже");
            absSender.execute(replyMessage);
        }
    }


    void execute(AbsSender sender, SendMessage message, User user) {
        try {
            sender.execute(message);
        } catch (TelegramApiException e) {
        }
    }
}
