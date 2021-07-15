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
import ru.kumkuat.application.GameModule.Bot.*;
import ru.kumkuat.application.GameModule.Service.SceneService;
import ru.kumkuat.application.GameModule.Service.TelegramChatService;
import ru.kumkuat.application.GameModule.Service.UserService;

@Slf4j
@Service
public class PlayCommand extends BotCommand {

    private final UserService userService;
    @Autowired
    private TelegramChatService telegramChatService;
    @Autowired
    private MarshakBot marshakBot;
    @Autowired
    private Harms harms;
    @Autowired
    private AkhmatovaBot akhmatovaBot;
    @Autowired
    private Brodskiy brodskiy;
    @Autowired
    private MayakBot mayakBot;
    @Autowired
    private SceneService sceneService;

    public PlayCommand(UserService userService) {
        super("/play", "После этой команды начнется игра");
        this.userService = userService;
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {

        long userId = user.getId().longValue();
        if (userId == marshakBot.getId() && userService.IsUserExist(chat.getId())) {
            userId = chat.getId();
        }

        if (userId == chat.getId()) {

            log.debug("Marshak ");
            SendMessage replyMessage = new SendMessage();
            replyMessage.setChatId(chat.getId().toString());
            replyMessage.enableHtml(true);

            try {
                if (!userService.IsUserExist(userId)) {
                    try {
                        userService.setUserIntoDB(user);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    replyMessage.setText("Вы успешно зарегистрировались!");
                    execute(absSender, replyMessage, user);
                } else {
                    if ( userService.IsUserHasPayment(userId)) {
                        SendFreeChat(absSender, Long.valueOf(userId));
                    } else {
                        replyMessage.setText("Необходимо внести оплату!");
                        execute(absSender, replyMessage, user);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    boolean isBotsStarting(AbsSender absSender, Long userId) throws TelegramApiException {
        SendMessage replyMessage = new SendMessage();
        replyMessage.setChatId(userId.toString());
        replyMessage.enableHtml(true);
        boolean result = true;
        String reply = "";
        if (!harms.isBotsStarting(userId.toString())) {
            reply += /*"\n@" + harms.getBotUsername() + */"\nХармс не активирован";
            result = false;
        }
        if (!akhmatovaBot.isBotsStarting(userId.toString())) {
            reply += /*"\n@" + akhmatovaBot.getBotUsername() + */"\nАхматова не активирована";
            result = false;
        }
        if (!brodskiy.isBotsStarting(userId.toString())) {
            reply += /*"\n@" + brodskiy.getBotUsername() + */"\nБродский не активирован";
            result = false;
        }
        if (!mayakBot.isBotsStarting(userId.toString())) {
            reply += /*"\n@" + mayakBot.getBotUsername() + */"\nМаяковский не активирован";
            result = false;
        }
        if (!result) {
            replyMessage.setText(reply);
            absSender.execute(replyMessage);
        }
        return result;
    }
/** What will happend when user end the game and press Start Promenad, but didn't pay again? */
    void SendFreeChat(AbsSender absSender, Long userId) throws Exception {
        SendMessage replyMessage = new SendMessage();
        replyMessage.setChatId(userId.toString());
        replyMessage.enableHtml(true);
        if (userService.IsUserExist(userId.longValue())) {
            if (!telegramChatService.isUserAlreadyPlaying(userId) ) {
                if (isBotsStarting(absSender, Long.valueOf(userId))) {

                    if(userService.getUserByTelegramId(userId).getSceneId() < 1) {
                        userService.setPlaying(Long.valueOf(userId), true);
                    replyMessage.setText("Отлично! Как я могу к тебе обращаться? ");
                    absSender.execute(replyMessage); }
                    else if(userService.getUserByTelegramId(userId).getSceneId() == sceneService.count()) {
                        replyMessage.setText("Вы окончили прогулку, поздравляю! Чтобы начать прогулку заново обратитесь к администратору (/help)");
                        absSender.execute(replyMessage);
                    } /* тут должна отрабатвать проверка на оплату,
                         чтобы сюда пользователь не попадал, а пока закроем дырку так.  */
                    else  {
                        userService.setPlaying(Long.valueOf(userId), true);
                        replyMessage.setText("Понеслась душа в рай!");
                        marshakBot.getSendChatCommand().SendFreeChat(absSender, userId); /** Sending Link to chat if User didn't finish the Game **/

                    }
//                    if (telegramChatService.isFreeChatHas()) {
//
//                        var freeChat = telegramChatService.getFreeChat();
//                        freeChat.setBusy(true);
//                        //freeChat.setStartPlayTime(new Date());
//                        freeChat.setUserId(userId.longValue());
//                        telegramChatService.saveChatIntoDB(freeChat);
//
//                        ExportChatInviteLink exportChatInviteLink = new ExportChatInviteLink(freeChat.getChatId().toString());
//                        var inviteLink = absSender.execute(exportChatInviteLink);
//
//                        replyMessage.setText("Присоединяйся! Для старта напиши \"Привет\"");
//                        absSender.execute(replyMessage);
//                        replyMessage.setText(inviteLink);
//                        absSender.execute(replyMessage);
//                        //нужно сделать проверку что пользователь играет в беседке, которая зарезирвирована. Что он вошел в беседку.
//
//                    } else {
//                        replyMessage.setText("Нет свободных чатов, попробуйте позже");
//                        absSender.execute(replyMessage);
//                    }
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
