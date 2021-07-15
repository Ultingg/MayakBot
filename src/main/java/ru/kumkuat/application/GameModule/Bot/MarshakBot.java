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
import ru.kumkuat.application.GameModule.Repository.UserRepository;
import ru.kumkuat.application.GameModule.Service.TelegramChatService;
import ru.kumkuat.application.GameModule.Service.TimerService;
import ru.kumkuat.application.GameModule.Service.UserService;

import java.util.*;

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
    private KickCommand kickAllCommand;
    @Autowired
    private SupportCommand supportCommand;
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

    private String secretName = "Marshak";

    @Value("${marshak.name}")
    private String botUsername;
    @Value("${marshak.token}")
    private String botToken;
    @Value("${marshak.path}")
    private String botPath;
    @Value("${marshak.id}")
    private Double Id;
    @Value("${time.hour.offset}")
    private int TimeOffset;

    private MarshakBot() {

    }

    public void TimerOperation() {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(telegramChatService.getAdminChatId());
        sendMessage.setText("Выполнено отложенное задание: " + new Date());
        this.sendMessage(sendMessage);
        var busyChatsList = telegramChatService.getBusyChats();
        for (var busyChat :
                busyChatsList) {
            var player = userService.getUserByTelegramId(busyChat.getUserId());
            player.setTriggered(false);
            player.setSceneId(0L);
            player.setHasPay(false);
            player.setPlaying(false);
            userRepository.save(player);
            //resetUserCommand.execute(this, null, null, new String[]{player.getTelegramUserId().toString()} );
        }
        kickAllCommand.KickAllChatMember(this);
    }

    private void StartTimer() {
        //http://java-online.ru/java-calendar.xhtml   //TODO УБРАТЬ ЭТОТ МЕТОД в TIME SERVICE ОН ЗДЕСЬ ГВОЗДИТ

        final String TIMEZONE_msc = "Europe/Moscow";

        //Создаем календари
        Calendar calendar_curr = new GregorianCalendar();
        Calendar calendar_midnight = new GregorianCalendar();

        //Инициируем верменные зоны
        TimeZone tm_curr = TimeZone.getDefault(); //Временная зона сервера
        TimeZone tm_msk = TimeZone.getTimeZone(TIMEZONE_msc); //Временная зона Москвы

        //Переводим время к москве
        calendar_midnight.add(Calendar.SECOND, (tm_msk.getRawOffset() - tm_curr.getRawOffset()) / 1000);
        calendar_curr.add(Calendar.SECOND, (tm_msk.getRawOffset() - tm_curr.getRawOffset()) / 1000);

        //Устанавливаем полночь
        calendar_midnight.set(Calendar.HOUR_OF_DAY, TimeOffset);
        calendar_midnight.set(Calendar.MINUTE, 0);
        calendar_midnight.set(Calendar.SECOND, 0);
        calendar_midnight.add(Calendar.DAY_OF_WEEK,1);

        long timeDelay = Math.abs(calendar_curr.getTime().getTime() - calendar_midnight.getTime().getTime());

        Timer timer = new Timer(true);
        TimerService timerService = new TimerService();

        timerService.setTimerOperation(() -> TimerOperation());
        timer.scheduleAtFixedRate(timerService, timeDelay, 24 * 60 * 60 * 1000);

        int hours = (int)(timeDelay / 1000) / (60 * 60);
        int minutes = (int)(((timeDelay / 1000) % (60d * 60d)) / 60);

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(telegramChatService.getAdminChatId());
        sendMessage = new SendMessage();
        sendMessage.setChatId(telegramChatService.getAdminChatId());
        sendMessage.setText("Очистка беседок через: " + hours + " часов " + minutes + " минут");
        this.sendMessage(sendMessage);
    }

    @Override
    public void afterPropertiesSet() {
        RegisterCommand();
        StartTimer();
    }

    public void RegisterCommand() {
        register(playCommand);
        register(payCommand);
        register(playMarathonCommand);
        register(resetCommand);
        register(saveChatCommand);
        register(kickAllCommand);
        register(nextSceneCommand);
        register(previousSceneCommand);
        register(supportCommand);
        register(startCommand);
        register(helpCommand);
        register(sendChatCommand);
        register(resetUserCommand);
        register(setSceneNumberCommand);
        register(sendMailCommand);
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
//                replyMessage.setText("Вcе готово, чтобы начать!\n" +
//                        "Осталось активировать ботов:\n" +
//                        "@" + mayakBot.getBotUsername() + "\n" +
//                        "@" + akhmatovaBot.getBotUsername() + "\n" +
//                        "@" + brodskiy.getBotUsername() + "\n" +
//                        "@" + harms.getBotUsername() + "\n" +ss
//                        "и нажать /play"
//                );

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
