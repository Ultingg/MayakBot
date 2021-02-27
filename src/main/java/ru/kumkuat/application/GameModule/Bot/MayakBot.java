package ru.kumkuat.application.GameModule.Bot;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendLocation;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Location;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.kumkuat.application.GameModule.Geolocation.GeoLocationUtils;
import ru.kumkuat.application.GameModule.Geolocation.Geolocation;

import java.util.Map;

@Slf4j
@Setter
@Getter
@Component
@NoArgsConstructor
@AllArgsConstructor
@PropertySource(name = "secret.yml", value = "secret.yml" )
public class MayakBot extends TelegramLongPollingBot {
    @Value("${bot.name}")
    private String botUsername;
    @Value("${bot.token}")
    private String botToken;

    @Autowired
    private ru.kumkuat.application.GameModule.Controller.BotController botController;

    @Autowired
    private GeoLocationUtils geoLocationUtils;


    public MayakBot(GeoLocationUtils geoLocationUtils) {
        this.geoLocationUtils = geoLocationUtils;
    }


    @Override
    public void onUpdateReceived(Update update) {

        String message = update.getMessage().getText();
        botController.chooser(message, update);
//        String message = update.getMessage().getText();
//        Long chatId = update.getMessage().getChatId();
//        Integer messageId = update.getMessage().getMessageId();
//
//        if (update.getMessage().hasLocation()) {
//            Location userLocation = update.getMessage().getLocation();
//            sendLocation(userLocation, chatId.toString(), messageId);
//
//        } else {
//            StringBuilder dd =new StringBuilder();
//            sendMsg(chatId.toString(), message, messageId);
//        }
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
        Map<String, Object> resultList = geoLocationUtils.foundNearestLocationService(userLocation);
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


}
