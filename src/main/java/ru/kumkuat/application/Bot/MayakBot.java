package ru.kumkuat.application.Bot;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendLocation;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Location;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.kumkuat.application.Geolocation.GeoLocationUtils;
import ru.kumkuat.application.Geolocation.Geolocation;

import java.util.Map;

@Slf4j
@Setter
@Getter
@Component
@NoArgsConstructor
@AllArgsConstructor

public class MayakBot extends TelegramLongPollingBot {
    @Value("${bot.name}")
    private String botUsername;
    @Value("${bot.token}")
    private String botToken;

    @Autowired
    private ru.kumkuat.application.Config.BotController botController;

    @Autowired
    private GeoLocationUtils geoLocationUtils;


    public MayakBot(GeoLocationUtils geoLocationUtils) {
        this.geoLocationUtils = geoLocationUtils;
    }


    @Override
    public void onUpdateReceived(Update update) {

//        String message = update.getMessage().getText();
//        botController.chooser(message);
        String message = update.getMessage().getText();
        Long chatId = update.getMessage().getChatId();
        Integer messageId = update.getMessage().getMessageId();

        if (update.getMessage().hasLocation()) {
            Location userLocation = update.getMessage().getLocation();
            //Geolocation neasrestLocation =  getNearestLocation(userLocation);
            sendLocation(userLocation, chatId.toString(), messageId);

        } else {
            StringBuilder dd =new StringBuilder();
            sendMsg(chatId.toString(), message, messageId);
        }
    }



    public synchronized void sendMsg(String chatId, String s) {
        SendMessage sendMessage = SendMessage.builder().chatId(chatId).text("Я достаю из широких штанин!!!").build();
        sendMessage.enableMarkdown(true);
        System.out.println(chatId);
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

//    public synchronized void sendLocation(Update update) {
//        Double petroLatitude = 59.950157;
//        Double petroLongitude = 30.315352;
//        Double userLatitude = update.getMessage().getLocation().getLatitude();
//        Double userLongitude = update.getMessage().getLocation().getLongitude();
//        Double distance = GeoLocationUtils.distanceToCurrentLocation(userLatitude, userLongitude, petroLatitude, petroLongitude);
//        String message = String.format("От тебя до Петропавловской крепости: %d метров", distance.intValue());
//
//        sendMsg(update.getMessage().getChatId().toString(), message, update.getMessage().getMessageId());
//    }



//    public synchronized void sendLocation(Geolocation geolocation, String chatId, Integer id) {
//        SendLocation sendLocation = SendLocation.builder()
//                .longitude(geolocation.getLongitude())
//                .latitude(geolocation.getLatitude())
//                .chatId(chatId)
//                .build();
//        String message = String.format("Ближайшая к вам локация: %s, до нее %.2f метров"
//                , geolocation.getFullName(), geolocation.getDistance());
//
//        SendMessage sendMessage = SendMessage.builder()
//                .chatId(chatId).replyToMessageId(id).text(message).build();
//        try {
//            execute(sendLocation);
//            execute(sendMessage);
//        } catch (TelegramApiException e) {
//        }
//    }

//    public synchronized Geolocation getNearestLocation(Location userLocation) {
//
//        Map<String, Location> locationMap = new HashMap<>();
//        Location petroKrepost = new Location();
//        petroKrepost.setLatitude(59.950157);
//        petroKrepost.setLongitude(30.315352);
//        Location zimniy = new Location();
//        zimniy.setLatitude(59.939916);
//        zimniy.setLongitude(30.314699);
//        Location moscowRailwaySt = new Location();
//        moscowRailwaySt.setLatitude(59.930102);
//        moscowRailwaySt.setLongitude(30.362520);
//        locationMap.put("PetroKrepost", petroKrepost);
//        locationMap.put("Zimniy", zimniy);
//        locationMap.put("MoscowRailwaySt", moscowRailwaySt);
//
//        return GeoLocationUtils.nearestLocation(locationMap, userLocation);
//
//
//    }

}
