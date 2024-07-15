package ru.kumkuat.application.gameModule.bot;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand;
import org.telegram.telegrambots.meta.api.methods.AnswerPreCheckoutQuery;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.*;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.kumkuat.application.gameModule.Abstract.TelegramWebhookCommandBot;
import ru.kumkuat.application.gameModule.marshakCommands.IListenerSupport;
import ru.kumkuat.application.gameModule.promocode.Service.PromocodeLogeService;
import ru.kumkuat.application.gameModule.repository.UserRepository;
import ru.kumkuat.application.gameModule.service.PaymentService;
import ru.kumkuat.application.gameModule.service.TelegramChatService;
import ru.kumkuat.application.gameModule.service.UserService;

import java.util.List;

@Component
@Slf4j
@Setter
@Getter
@PropertySource(value = "file:../resources/externalsecret.yml")
public class MarshakBot extends TelegramWebhookCommandBot implements BotsSender, InitializingBean {

    @Value("${marshak.secretName}")
    private String secretName;
    @Value("${marshak.name}")
    private String botUsername;
    @Value("${marshak.token}")
    private String botToken;
    @Value("${marshak.path}")
    private String botPath;
    @Value("${marshak.id}")
    private int Id;
    @Value("${time.hour.offset}")
    private int TimeOffset;

    @Autowired
    private final List<IBotCommand> commands;

    private final TelegramChatService telegramChatService;
    private final UserService userService;
    private final PaymentService paymentService;
    private final UserRepository userRepository;
    private final PromocodeLogeService promocodeLogeService;

    private MarshakBot(List<IBotCommand> commands, TelegramChatService telegramChatService,
                       UserService userService, PaymentService paymentService,
                       UserRepository userRepository, PromocodeLogeService promocodeLogeService) {
        this.commands = commands;
        this.telegramChatService = telegramChatService;
        this.userService = userService;
        this.paymentService = paymentService;
        this.userRepository = userRepository;
        this.promocodeLogeService = promocodeLogeService;
    }


    @Override
    public void afterPropertiesSet() {
        RegisterCommand();
    }

    public void RegisterCommand() {
        commands.forEach(this::register);

        for (var command :
                getRegisteredCommands()) {
            if (command instanceof IListenerSupport) {
                ((IListenerSupport) command).addListener(this);
            }
        }
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
                userService.setUserPayment(user.getId(), true);
                replyMessage.setText("Отлично! Вcе готово, чтобы начать!");
                promocodeLogeService.pomocodeLogging(user);
                execute(replyMessage);
                paymentService.processPayment(update);
                log.info("Paid was done by user with id: {}", user.getId());

            } catch (Exception e) {
                log.info("Payment of user {} faild", user.getId(), e);
            }
        }
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public void sendLocation(SendLocation sendLocation) {
        try {
            execute(sendLocation);
        } catch (TelegramApiException e) {
            log.debug("{} failed sending SendLocationMessage!", secretName);
            e.getStackTrace();
        }
    }

    @Override
    public void sendVoice(SendVoice sendVoice) {
        try {
            execute(sendVoice);
        } catch (TelegramApiException e) {
            e.getStackTrace();
            log.debug("{} failed sending SendVoiceMessage!", secretName);
        }
    }

    @Override
    public void sendPicture(SendPhoto sendPhoto) {
        try {
            execute(sendPhoto);
        } catch (TelegramApiException e) {
            e.getStackTrace();
            log.debug("{} failed sending SendPhotoMessage!", secretName);
        }
    }

    @Override
    public void sendMessage(SendMessage sendMessage) {
        try {

            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.getStackTrace();
            log.debug("{} failed sending SendTextMessage!", secretName);
        }
    }

    @Override
    public void sendSticker(SendSticker sendSticker) {
        try {
            execute(sendSticker);
        } catch (TelegramApiException e) {
            e.getStackTrace();
            log.debug("{} failed sending SendTextMessage!", secretName);
        }
    }

    public void sendDocument(SendDocument sendDocument) {
        try {
            execute(sendDocument);
        } catch (TelegramApiException e) {
            e.getStackTrace();
            log.debug("{} failed sending SendDocument!", secretName);
        }
    }

    public String getSecretName() {
        return secretName;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public String getBotPath() {
        return botPath;
    }
}
