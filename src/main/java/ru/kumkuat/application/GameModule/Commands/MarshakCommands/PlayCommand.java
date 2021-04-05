package ru.kumkuat.application.GameModule.Commands.MarshakCommands;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.groupadministration.ExportChatInviteLink;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.kumkuat.application.GameModule.Bot.AkhmatovaBot;
import ru.kumkuat.application.GameModule.Bot.Brodskiy;
import ru.kumkuat.application.GameModule.Bot.Harms;
import ru.kumkuat.application.GameModule.Bot.MayakBot;
import ru.kumkuat.application.GameModule.Service.TelegramChatService;
import ru.kumkuat.application.GameModule.Service.UserService;

@Slf4j
@Service
public class PlayCommand extends BotCommand {

    private final UserService userService;
    @Autowired
    private TelegramChatService telegramChatService;
    @Autowired
    private Harms harms;
    @Autowired
    private AkhmatovaBot akhmatovaBot;
    @Autowired
    private Brodskiy brodskiy;
    @Autowired
    private MayakBot mayakBot;

    public PlayCommand(UserService userService) {
        super("/play", "После этой команды начнется игра");
        this.userService = userService;
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        if (user.getId().longValue() == chat.getId()) {

            log.debug("Marshak ");
            SendMessage replyMessage = new SendMessage();
            replyMessage.setChatId(chat.getId().toString());
            replyMessage.enableHtml(true);

            /*if (user.getUserName() == null) {
                replyMessage.setText("Ты человек без имени. С тобой играть не получится. Разберись в себе для начала...");
            } else if (user.getUserName().equals("GroupAnonymousBot")) {
                replyMessage.setText("Нужно выключить ананонимность. Ты не бэтмэн! Сними маску -_-");
            } else */
            if (!userService.IsUserExist(user.getId().longValue())) {
                try {
                    userService.setUserIntoDB(user);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                replyMessage.setText("Вы успешно зарегистрировались!");
            }
            execute(absSender, replyMessage, user);
            try {
                if (userService.IsUserExist(user.getId().longValue())) {
                    if (userService.IsUserHasPayment(user.getId().longValue())) {
                        if (isBotsStarting(absSender, user, chat)) {
                            SendFreeChat(absSender, user, chat);
                        }
                    }
                    else{
                        replyMessage.setText("Необходимо внести оплату!");
                        execute(absSender, replyMessage, user);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    boolean isBotsStarting(AbsSender absSender, User user, Chat chat) throws TelegramApiException {
        SendMessage replyMessage = new SendMessage();
        replyMessage.setChatId(chat.getId().toString());
        replyMessage.enableHtml(true);
        boolean result = true;
        String reply = "";
//        if (!harms.isBotsStarting(user.getId().toString())) {
//            reply += "\n@" + harms.getBotUsername() + " не активирован";
//            result = false;
//        }
        if (!akhmatovaBot.isBotsStarting(user.getId().toString())) {
            reply += "\n@" + akhmatovaBot.getBotUsername() + " не активирован";
            result = false;
        }
        if (!brodskiy.isBotsStarting(user.getId().toString())) {
            reply += "\n@" + brodskiy.getBotUsername() + " не активирован";
            result = false;
        }
        if (!mayakBot.isBotsStarting(user.getId().toString())) {
            reply += "\n@" + mayakBot.getBotUsername() + " не активирован";
            result = false;
        }
        if(!result){
            replyMessage.setText(reply);
            absSender.execute(replyMessage);
        }
        return result;
    }

    void SendFreeChat(AbsSender absSender, User user, Chat chat) throws Exception {
        SendMessage replyMessage = new SendMessage();
        replyMessage.setChatId(chat.getId().toString());
        replyMessage.enableHtml(true);
        if (userService.IsUserExist(user.getId().longValue())) {
            if (!telegramChatService.isUserAlreadyPlaying(user)) {
                if (telegramChatService.isFreeChatHas()) {

                    var freeChat = telegramChatService.getFreeChat();
                    freeChat.setBusy(true);
                    //freeChat.setStartPlayTime(new Date());
                    freeChat.setUserId(user.getId().longValue());
                    telegramChatService.saveChatIntoDB(freeChat);

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
        }
    }
}
