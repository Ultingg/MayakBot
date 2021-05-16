package ru.kumkuat.application.GameModule.Bot;

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
import ru.kumkuat.application.GameModule.Abstract.TelegramWebhookCommandBot;
import ru.kumkuat.application.GameModule.Commands.MarshakCommands.*;
import ru.kumkuat.application.GameModule.Service.TelegramChatService;
import ru.kumkuat.application.GameModule.Service.TimerService;
import ru.kumkuat.application.GameModule.Service.UserService;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Timer;

@Component
@Slf4j
@Setter
@Getter
@PropertySource(value = "file:../resources/externalsecret.yml")
public class MarshakBot extends TelegramWebhookCommandBot implements BotsSender, InitializingBean {
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
    private ResetCommand resetCommand;
    @Autowired
    private SaveChatCommand saveChatCommand;
    @Autowired
    private NextSceneCommand nextSceneCommand;
    @Autowired
    private PreviousSceneCommand previousSceneCommand;
    @Autowired
    private KickAllCommand kickAllCommand;
    @Autowired
    private SupportCommand supportCommand;
    @Autowired
    private StartCommand startCommand;
    @Autowired
    private PlayCommand playCommand;

    private String secretName = "Marshak";

    @Value("${marshak.name}")
    private String botUsername;
    @Value("${marshak.token}")
    private String botToken;
    @Value("${marshak.path}")
    private String botPath;
    @Value("${marshak.id}")
    private Double Id;

    private MarshakBot() {

    }

    public void TimerOperation() {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(telegramChatService.getAdminChatId());
        sendMessage.setText("Выполнено отложенное задание: " + new Date());
        this.sendMessage(sendMessage);
        kickAllCommand.KickAllChatMember(this, telegramChatService.getAdminChatId());
    }

    private void StartTimer() {
        Timer timer = new Timer(true);
        TimerService timerService = new TimerService();
        //Calendar calendar = new GregorianCalendar(2021, Calendar.APRIL, 11, 21, 50);

        Calendar calendarOut = new GregorianCalendar();
        //calendar.add(Calendar.DAY_OF_WEEK, +1);
        calendarOut.set(Calendar.HOUR, 12);
        calendarOut.set(Calendar.MINUTE, 0);
        calendarOut.set(Calendar.SECOND, 0);

        Calendar calendarCurrentDate = new GregorianCalendar();
        calendarCurrentDate.setTimeInMillis(new GregorianCalendar().getTimeInMillis());

        //calendar.setTimeInMillis(calendar.getTimeInMillis() + 10800);
        Long delay = calendarOut.getTimeInMillis() - calendarCurrentDate.getTimeInMillis();
        timerService.setTimerOperation(() -> TimerOperation());
        timer.scheduleAtFixedRate(timerService, delay, 24 * 60 * 60 * 1000);

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(telegramChatService.getAdminChatId());
        sendMessage.setText("Очистка беседок в: " + calendarOut.getTime());
        this.sendMessage(sendMessage);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        RegisterCommand();
        StartTimer();
    }

    public void RegisterCommand() {
        register(playCommand);
        register(payCommand);
        register(resetCommand);
        register(saveChatCommand);
        register(kickAllCommand);
        register(nextSceneCommand);
        register(previousSceneCommand);
        register(supportCommand);
        register(startCommand);
        register(helpCommand);
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
                replyMessage.setText("Вcе готово, чтобы начать!\n" +
                        "Осталось активировать ботов:\n" +
                        "@" + mayakBot.getBotUsername() + "\n" +
                        "@" + akhmatovaBot.getBotUsername() + "\n" +
                        "@" + brodskiy.getBotUsername() + "\n" +
                        "@" + harms.getBotUsername() + "\n" +
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
}
