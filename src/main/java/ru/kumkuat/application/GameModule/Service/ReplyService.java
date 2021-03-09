package ru.kumkuat.application.GameModule.Service;

import org.telegram.telegrambots.meta.api.objects.Location;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.kumkuat.application.GameModule.Collections.Reply;
import ru.kumkuat.application.GameModule.Collections.ReplyCollection;
import ru.kumkuat.application.GameModule.Geolocation.Geolocation;

public class ReplyService {

    private final ReplyCollection replyCollection;
    private final AudioService audioService;
    private final PictureService pictureService;
    private final GeolocationService geolocationService;


    public ReplyService(ReplyCollection replyCollection, AudioService audioService, PictureService pictureService, GeolocationService geolocationService) {
        this.replyCollection = replyCollection;
        this.audioService = audioService;
        this.pictureService = pictureService;
        this.geolocationService = geolocationService;
    }

    public Message getMessage(Long id) {
        Message resultMessage = new Message();
        Reply reply = replyCollection.getReply(id);

        if (reply.hasGeolocation()) {
            Long geolocationId = reply.getGeolocationId();
            Geolocation geolocation = geolocationService.getGeolocationById(geolocationId);

            Location location = new Location();
            location.setLongitude(geolocation.getLongitude());
            location.setLatitude(geolocation.getLatitude());
            resultMessage.setLocation(location);

        }
        if (reply.hasText()) resultMessage.setText(reply.getTextMessage());
//        if (reply.hasPicture()) resultMessage.setPhoto();

        return resultMessage;
    }

    public String senderDetector(Long id) {
        Reply reply = replyCollection.getReply(id);
        String senderName = reply.getBotName();
        return senderName;
    }
}
