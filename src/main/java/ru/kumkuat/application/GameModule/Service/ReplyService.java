package ru.kumkuat.application.GameModule.Service;

import org.telegram.telegrambots.meta.api.objects.Location;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.kumkuat.application.GameModule.Collections.Reply;
import ru.kumkuat.application.GameModule.Collections.ReplyCollection;

public class ReplyService {

    private final ReplyCollection replyCollection;
    private final AudioService audioService;
    private final PictureService pictureService;


    public ReplyService(ReplyCollection replyCollection, AudioService audioService, PictureService pictureService) {
        this.replyCollection = replyCollection;
        this.audioService = audioService;
        this.pictureService = pictureService;
    }

    //  RepliesCollection = { rep1, rep2, rep3, rep 4};
    public Message getMessage(Long id) {
        Message resultMessage = new Message();
        Reply reply = replyCollection.getReply(id);
//        if (reply.hasAudio()) resultMessage.setAudio();
        if (reply.hasGelocation()) {
            Location location = new Location();
            location.setLongitude(reply.getGeolocation().getLongitude());
            location.setLatitude(reply.getGeolocation().getLatitude());
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
