package ru.kumkuat.application.GameModule.Bot;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerPreCheckoutQuery;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.groupadministration.ExportChatInviteLink;
import org.telegram.telegrambots.meta.api.methods.send.SendLocation;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendVoice;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.kumkuat.application.GameModule.Abstract.TelegramWebhookCommandBot;
import ru.kumkuat.application.GameModule.Commands.MarshakCommands.*;
import ru.kumkuat.application.GameModule.Service.TelegramChatService;
import ru.kumkuat.application.GameModule.Service.UserService;

@Slf4j
@Setter
@Getter
@Component
@PropertySource(name = "secret.yml", value = "secret.yml")
public class MarshakBot extends TelegramWebhookCommandBot implements BotsSender {

    @Autowired
    private TelegramChatService telegramChatService;
    @Autowired
    private UserService userService;

    private String secretName = "Marshak";

    @Value("${marshak.name}")
    private String botUsername;
    @Value("${marshak.token}")
    private String botToken;
    @Value("${marshak.path}")
    private String botPath;

//    @Autowired
//    private PlayCommand playCommand;
//    @Autowired
//    private PayCommand payCommand;
//    @Autowired
//    private ResetCommand resetCommand;
//    @Autowired
//    private SaveChatCommand saveChatCommand;

    private MarshakBot(PlayCommand playCommand, PayCommand payCommand, ResetCommand resetCommand,
                       SaveChatCommand saveChatCommand, NextSceneCommand nextSceneCommand,
                       PreviousSceneCommand previousSceneCommand, HelpCommand helpCommand,
                       KickAllCommand kickAllCommand, SupportCommand supportCommand,
                       StartCommand startCommand) {
        register(playCommand);
        register(payCommand);
        register(resetCommand);
        register(saveChatCommand);
        register(kickAllCommand);
        register(nextSceneCommand);
        register(previousSceneCommand);
        register(helpCommand);
        register(supportCommand);
        register(startCommand);
    }

    @Override
    public BotApiMethod processNonCommandUpdate(Update update) {
        if (update.hasPreCheckoutQuery()) {
            AnswerPreCheckoutQuery answerPreCheckoutQuery = new AnswerPreCheckoutQuery();
            answerPreCheckoutQuery.setPreCheckoutQueryId(update.getPreCheckoutQuery().getId());
            answerPreCheckoutQuery.setOk(true);
            try {
                this.execute(answerPreCheckoutQuery);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    public void DoAfterSuccessfulPayment(Update update) {
        if (update.hasMessage()) {
            User user = update.getMessage().getFrom();
            SendMessage replyMessage = new SendMessage();
            replyMessage.setChatId(update.getMessage().getChatId().toString());
            replyMessage.enableHtml(true);
            try {
                userService.setUserPayment(user.getId(),true);
                replyMessage.setText("Вcе готово, чтобы начать!\n" +
                        "Осталось активировать ботов:\n" +
                        "@VlVlMayakovskiyTestBot\n" +
                        "@AnAnAkhmatovaTestBot\n" +
                        "@IABrodskiyTestBot\n" +
                        "@DaIvHarmsTestBot\n" +
                        "и нажать /play"
                );
                execute(replyMessage);
                //SendFreeChat(user, update.getMessage().getChat());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void sendLocation(SendLocation sendLocation) {
        log.debug("{} get SendLocationMessage!", secretName);
        try {
            execute(sendLocation);
            log.debug("{} send SendLocationMessage!", secretName);
        } catch (TelegramApiException e) {
            log.debug("{} failed sending SendLocationMessage!", secretName);
            e.getStackTrace();
        }
    }

    public void sendVoice(SendVoice sendVoice) {
        log.debug("{} get SendVoiceMessage!", secretName);
        try {
            execute(sendVoice);
            log.debug("{} send SendVoiceMessage!", secretName);
        } catch (TelegramApiException e) {
            e.getStackTrace();
            log.debug("{} failed sending SendVoiceMessage!", secretName);
        }
    }

    public void sendPicture(SendPhoto sendPhoto) {
        log.debug("{} get SendPhotoMessage!", secretName);
        try {
            execute(sendPhoto);
            log.debug("{} send SendPhotoMessage!", secretName);
        } catch (TelegramApiException e) {
            e.getStackTrace();
            log.debug("{} failed sending SendPhotoMessage!", secretName);
        }
    }

    public void sendMessage(SendMessage sendMessage) {
        log.debug("{} get SendTextMessage!", secretName);
        try {
            execute(sendMessage);
            log.debug("{} send SendTextMessage!", secretName);
        } catch (TelegramApiException e) {
            e.getStackTrace();
            log.debug("{} failed sending SendTextMessage!", secretName);
        }
    }
}
