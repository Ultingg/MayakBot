package ru.kumkuat.application.GameModule.Service;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Location;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.kumkuat.application.GameModule.Collections.Reply;
import ru.kumkuat.application.GameModule.Collections.Scene;
import ru.kumkuat.application.GameModule.Collections.Trigger;
import ru.kumkuat.application.GameModule.Geolocation.GeoLocationUtils;
import ru.kumkuat.application.GameModule.Geolocation.Geolocation;

import java.util.List;
import java.util.Map;

@Service
public class ResponseService {

    List<Scene> sceneList;
    private final GeoLocationUtils geoLocationUtils;

    public ResponseService(GeoLocationUtils geoLocationUtils) {
        this.geoLocationUtils = geoLocationUtils;
    }


    public void checkIncomingMessage(Message message, Long sceneId) {
        Scene scene = sceneList.get(sceneId.intValue());

        Trigger sceneTrigger = scene.getTrigger();
        if (sceneTrigger.triggerCheck(message)) ReplyResolver(message, scene);
        if (message.hasLocation()) {
            Location userLocation = message.getLocation();
            if (sceneTrigger.triggerCheck(userLocation)) ReplyResolver(message, scene);
        }
        if (message.hasText()) {
            String userText = message.getText();
            if (sceneTrigger.triggerCheck(userText)) ReplyResolver(message, scene);
        }
    }

    public void ReplyResolver(Message message, Scene scene) {
        List<Reply> replyList = scene.getReplyCollection();



    }

    public void configuringMessage(Reply reply, Message message) {

        if (message.hasLocation()) {
            Location userLocation = message.getLocation();
            Map<String, Object> resultList = geoLocationUtils.foundNearestLocationService(userLocation);
            Double distance = (Double) resultList.get("Distance");
            Geolocation resultGeolocation = (Geolocation) resultList.get("Geolocation");
        }

    }

}
