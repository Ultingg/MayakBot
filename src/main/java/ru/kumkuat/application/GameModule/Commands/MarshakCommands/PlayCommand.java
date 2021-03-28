package ru.kumkuat.application.GameModule.Commands.MarshakCommands;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.groupadministration.KickChatMember;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.kumkuat.application.GameModule.Service.TelegramChatService;
import ru.kumkuat.application.GameModule.Service.UserService;

import java.util.Date;

@Slf4j
@Service
public class PlayCommand extends BotCommand {

    private final UserService userService;
    @Autowired
    private TelegramChatService telegramChatService;

    public PlayCommand(UserService userService) {
        super("/play", "Write that command and lets get to play!\n");
        this.userService = userService;
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        if(user.getId().longValue() == chat.getId()){

            log.debug("Marshak ");
            SendMessage replyMessage = new SendMessage();
            replyMessage.setChatId(chat.getId().toString());
            replyMessage.enableHtml(true);



            if (user.getUserName() == null) {
                replyMessage.setText("Ты человек без имени. С тобой играть не получится. Разберись в себе для начала...");
            } else if (user.getUserName().equals("GroupAnonymousBot")) {
                replyMessage.setText("Нужно выключить ананонимность. Ты не бэтмэн! Сними маску -_-");
            } else if (!userService.IsUserExist(user.getUserName())) {
                try {
                    userService.setUserIntoDB(user);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                replyMessage.setText("Вы успешно зарегистрировались!");
            }
            execute(absSender, replyMessage, user);
            if( userService.IsUserExist(user.getUserName())){
                if(!telegramChatService.isUserAlreadyPlaying(user)){
                    if(telegramChatService.isFreeChatHas() ){
                        try {
                            var freeChat = telegramChatService.getFreeChat();
                            freeChat.setBusy(true);
                            freeChat.setStartPlayTime(new Date());
                            freeChat.setUserId(user.getId().longValue());
                            telegramChatService.saveChatIntoDB(freeChat);
                            replyMessage.setText(freeChat.getInviteLink());
                            execute(absSender, replyMessage, user);
                            replyMessage.setText("Присоединяйся!");
                            execute(absSender, replyMessage, user);
                            replyMessage.setText(freeChat.getInviteLink());
                            execute(absSender, replyMessage, user);
                            //нужно сделать проверку что пользователь играет в беседке, которая зарезирвирована
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    else{
                        replyMessage.setText("Нет свободных чатов, попробуйте позже");
                        execute(absSender, replyMessage, user);
                    }
                } else{
                    replyMessage.setText("Вы уже начали игру. Чтобы начать заново, нужно ее закончить.");
                    execute(absSender, replyMessage, user);
                }
            }
        }
    }

    void execute(AbsSender sender, SendMessage message, User user) {
        try {
            sender.execute(message);
        } catch (TelegramApiException e) {
        }
    }
}
