package ru.kumkuat.application.GameModule.Bot;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendLocation;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendVoice;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Location;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.kumkuat.application.GameModule.Controller.UpdateController;
import ru.kumkuat.application.GameModule.Controller.WebHookController;
import ru.kumkuat.application.GameModule.Models.Geolocation;
import ru.kumkuat.application.GameModule.Service.AudioService;
import ru.kumkuat.application.GameModule.Service.GeoLocationUtilsService;

import java.io.File;
import java.nio.file.Path;
import java.util.Map;

@Slf4j
@Setter
@Getter
@Component
@PropertySource(name = "secret.yml", value = "secret.yml")
@PropertySource(name = "application.yml", value = "application.yml")
public class MayakBot extends TelegramLongPollingBot implements BotsSender {
    @Value("${bot.name}")
    private String botUsername;
    @Value("${bot.token}")
    private String botToken;
    @Value("${text.path}")
    private Path path;    // TODO: убери эту заглушку!

    @Autowired
    private ru.kumkuat.application.GameModule.Controller.BotController botController;

    @Autowired
    private GeoLocationUtilsService geoLocationUtilsService;

    private final WebHookController webHookController;
    private final UpdateController updateController;
    private final AudioService audioService;

    public MayakBot(GeoLocationUtilsService geoLocationUtilsService, WebHookController webHookController, UpdateController updateController, AudioService audioService) {
        this.geoLocationUtilsService = geoLocationUtilsService;
        this.webHookController = webHookController;
        this.updateController = updateController;
        this.audioService = audioService;
    }


    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {
        webHookController.onUpdateReceived(update);
//        String message = update.getMessage().getText();
//        botController.chooser(message, update);
        updateController.receiveUpdate(update);
        String message = update.getMessage().getText();
        Long chatId = update.getMessage().getChatId();
        Integer messageId = update.getMessage().getMessageId();

        if (update.getMessage().hasLocation()) {
            Location userLocation = update.getMessage().getLocation();
            sendLocation(userLocation, chatId.toString(), messageId);

        } else {

            sendVoice(chatId.toString());
            sendPicture(chatId.toString());
            sendMsg(chatId.toString(), message, messageId);
        }
    }

    public synchronized void sendVoice(String chatId) {
        InputFile voiceFile = new InputFile();
        String path = audioService.getPathToAudio(1L);
        File file = new File(path);

        voiceFile.setMedia(file);
        SendVoice sendVoice = new SendVoice();
        sendVoice.setChatId(chatId);
        sendVoice.setVoice(voiceFile);
        sendVoice.setDuration(150);

        try {
            execute(sendVoice);
        } catch (TelegramApiException e) {
            e.getStackTrace();
        }
    }

    public synchronized void sendPicture(String chatId) {
        InputFile pictureFile = new InputFile();
        File file = new File(path.toFile() + "\\tea.jpg");
        pictureFile.setMedia(file);
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(chatId);
        sendPhoto.setPhoto(pictureFile);


        try {
            execute(sendPhoto);
        } catch (TelegramApiException e) {
            e.getStackTrace();
        }

    }

    public synchronized void sendMsg(String chatId, String s) {
        SendMessage sendMessage = SendMessage.builder().chatId(chatId).text(s).build();
        sendMessage.enableMarkdown(true);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.getStackTrace();
        }
    }

    public synchronized void sendMsg(String chatId, String s, Integer id) {
        SendMessage sendMessage = SendMessage.builder().chatId(chatId).text("Я достаю из широких штанин!!!").build();
        sendMessage.enableMarkdown(true);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.getStackTrace();
        }
    }

    public synchronized void sendLocation(Location userLocation, String chatId, Integer id) {
        Map<String, Object> resultList = geoLocationUtilsService.foundNearestLocationService(userLocation);
        Double distance = (Double) resultList.get("Distance");
        Geolocation resultGeolocation = (Geolocation) resultList.get("Geolocation");
        SendLocation sendLocation = SendLocation.builder()
                .longitude(resultGeolocation.getLongitude())
                .latitude(resultGeolocation.getLatitude())
                .chatId(chatId)
                .build();
        String message = String.format("Ближайшая к вам локация: %s, до нее %.2f метров"
                , resultGeolocation.getFullName(), distance);

        SendMessage sendMessage = SendMessage.builder()
                .chatId(chatId).replyToMessageId(id).text(message).build();
        try {
            execute(sendLocation);
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.getStackTrace();
        }
    }

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
