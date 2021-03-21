package ru.kumkuat.application.GameModule.Bot;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendLocation;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendVoice;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.kumkuat.application.GameModule.Controller.BotController;
import ru.kumkuat.application.GameModule.Service.AudioService;
import ru.kumkuat.application.GameModule.Service.GeoLocationUtilsService;

@Slf4j
@Setter
@Getter
@Component
@PropertySource(name = "secret.yml", value = "secret.yml")
@PropertySource(name = "application.yml", value = "application.yml")
public class MayakBot extends TelegramWebhookBot implements BotsSender {
    @Value("${bot.name}")
    private String botUsername;
    @Value("${bot.token}")
    private String botToken;
    @Value("${text.path}")
    private String BotPath;
    // TODO: убери эту заглушку!

    @Autowired
    private BotController botController;

    //    @Autowired
    private final GeoLocationUtilsService geoLocationUtilsService;

    private final AudioService audioService;

    public MayakBot(GeoLocationUtilsService geoLocationUtilsService, AudioService audioService) {
        this.geoLocationUtilsService = geoLocationUtilsService;
        this.audioService = audioService;
    }
    @Override
    public BotApiMethod onWebhookUpdateReceived(Update update) {
        return new SendMessage();
    }

//    @SneakyThrows
//    @Override
//    public void onUpdateReceived(Update update) {
//    }



    public void sendLocation2(SendLocation sendLocation) {

        try {
            executeAsync(sendLocation);
        } catch (TelegramApiException e) {
            e.getStackTrace();
        }
    }

    public void sendVoice(SendVoice sendVoice) {
        try {
            execute(sendVoice);
        } catch (TelegramApiException e) {
            e.getStackTrace();
        }
    }

    public void sendPicture(SendPhoto sendPhoto) {
        try {
            execute(sendPhoto);
        } catch (TelegramApiException e) {
            e.getStackTrace();
        }
    }

    public void sendMessage(SendMessage sendMessage) {
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.getStackTrace();
        }
    }
}
