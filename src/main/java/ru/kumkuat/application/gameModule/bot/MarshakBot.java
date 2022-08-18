package ru.kumkuat.application.gameModule.bot;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerPreCheckoutQuery;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.*;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.kumkuat.application.gameModule.Abstract.TelegramWebhookCommandBot;
import ru.kumkuat.application.gameModule.marshakCommands.*;
import ru.kumkuat.application.gameModule.promocode.Service.PromocodeLogeService;
import ru.kumkuat.application.gameModule.repository.UserRepository;
import ru.kumkuat.application.gameModule.service.TelegramChatService;
import ru.kumkuat.application.gameModule.service.UserService;

import java.util.Date;

@Component
@Slf4j
@Setter
@Getter
@AllArgsConstructor
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
    private TelegramChatService telegramChatService;
    @Autowired
    private UserService userService;
    @Autowired
    private Harms harms;
    @Autowired
    private AkhmatovaBot akhmatovaBot;
    @Autowired
    private Brodskiy brodskiy;
    @Autowired
    private MayakBot mayakBot;
    @Autowired
    private HelpCommand helpCommand;
    @Autowired
    private PayCommand payCommand;
    @Autowired
    private SaveChatCommand saveChatCommand;
    @Autowired
    private KickCommand kickAllCommand;
    @Autowired
    private StartCommand startCommand;
    @Autowired
    private PlayCommand playCommand;
    @Autowired
    private PlayMarathonCommand playMarathonCommand;
    @Autowired
    private SendChatCommand sendChatCommand;
    @Autowired
    private ResetUserCommand resetUserCommand;
    @Autowired
    private SetSceneNumberCommand setSceneNumberCommand;
    @Autowired
    private SendMailCommand sendMailCommand;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private InputXSLXCommand inputXSLXCommand;
    @Autowired
    private ValidationReportCommand validationReportCommand;
    @Autowired
    private PromocodeLogeService promocodeLogeService;
    @Autowired
    private GeneratePCCommand generatePCCommand;
    @Autowired
    private GenerateMarkedCodeCommand generateMarkedCodeCommand;

    private MarshakBot() {
    }

    public void sendTimerOperationMessage(int quantityOfKickedUsers) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(telegramChatService.getAdminChatId());
        sendMessage.setText(String.format("Выполнено отложенное задание \"Очистка чатов\" " +
                "\nВыпровоженно гостей: %d. Время: %s", quantityOfKickedUsers, new Date()));
        this.sendMessage(sendMessage);
    }


    @Override
    public void afterPropertiesSet() {
        RegisterCommand();
    }

    public void RegisterCommand() {
        register(playCommand);
        register(payCommand);
        register(playMarathonCommand);
        register(saveChatCommand);
        register(kickAllCommand);
        register(startCommand);
        register(helpCommand);
        register(sendChatCommand);
        register(resetUserCommand);
        register(setSceneNumberCommand);
        register(sendMailCommand);
        register(inputXSLXCommand);
        register(validationReportCommand);
        register(generatePCCommand);
        register(generateMarkedCodeCommand);

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
                log.info("Paid was done bu user with id: {}", user.getId());

            } catch (Exception e) {
                log.info("Payment of user {} faild", user.getId(), e);
            }
        }
    }

    @Override
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

    @Override
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

    @Override
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

    @Override
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

    @Override
    public void sendSticker(SendSticker sendSticker) {
        log.debug("{} get SendTextMessage!", secretName);
        try {
            execute(sendSticker);
            log.debug("{} send SendTextMessage!", secretName);
        } catch (TelegramApiException e) {
            e.getStackTrace();
            log.debug("{} failed sending SendTextMessage!", secretName);
        }
    }

    public void sendDocument(SendDocument sendDocument) {
        log.debug("{} get SendDocument!", secretName);
        try {
            execute(sendDocument);
            log.debug("{} send SendDocument!", secretName);
        } catch (TelegramApiException e) {
            e.getStackTrace();
            log.debug("{} failed sending SendDocument!", secretName);
        }
    }

    public String getSecretName() {
        return secretName;
    }
}
