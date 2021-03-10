package ru.kumkuat.application.GameModule.Service;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendLocation;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendVoice;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Location;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.kumkuat.application.GameModule.Collections.Reply;
import ru.kumkuat.application.GameModule.Collections.ResponseContainer;
import ru.kumkuat.application.GameModule.Collections.Scene;
import ru.kumkuat.application.GameModule.Collections.Trigger;
import ru.kumkuat.application.GameModule.Controller.BotController;
import ru.kumkuat.application.GameModule.Models.Geolocation;
import ru.kumkuat.application.TemporaryCollections.SceneCollection;

import java.io.File;
import java.util.List;

@Service
public class ResponseService {

    private final SceneCollection sceneCollection;
    private final PictureService pictureService;
    private final AudioService audioService;
    private final BotController botController;
    private final GeolocationDatabaseService geolocationDatabaseService;
    private final TriggerService triggerService;

    public ResponseService(SceneCollection sceneCollection, PictureService pictureService,
                           AudioService audioService, BotController botController, GeolocationDatabaseService geolocationDatabaseService, TriggerService triggerService) {
        this.sceneCollection = sceneCollection;
        this.pictureService = pictureService;
        this.audioService = audioService;
        this.botController = botController;
        this.geolocationDatabaseService = geolocationDatabaseService;
        this.triggerService = triggerService;
    }


    public void checkIncomingMessage(Message message, Long sceneId) {
        Scene scene = sceneCollection.get(sceneId);
        Trigger sceneTrigger = scene.getTrigger();


        if (message.hasPhoto()) {
            if(sceneTrigger.isHasPicture()){
                ReplyResolver(message, scene);
            } else {
                ResponseContainer wrongAnswerResponse = configureWrongTriggerMessage(message,scene);
                botController.responseResolver(wrongAnswerResponse);
            }
        }

        if (message.hasLocation()) {
            Location userLocation = message.getLocation();
            if (triggerService.triggerCheck(sceneTrigger, userLocation)) {
                ReplyResolver(message, scene);
            }
        }
        if (message.hasText()) {
            String userText = message.getText();
            if (triggerService.triggerCheck(sceneTrigger, userText)) ReplyResolver(message, scene);
        }
    }

    public void ReplyResolver(Message message, Scene scene) {
        List<Reply> replyList = scene.getReplyCollection();
        ResponseContainer responseContainer;
        for (Reply reply : replyList) {
            responseContainer = configureMessage(reply, message);
            botController.responseResolver(responseContainer);
        }
    }

    public ResponseContainer configureWrongTriggerMessage(Message message, Scene scene) {
        //пока Message и Scene не используются но при реализации индивидуальных ответов для каждого триггера они понадобятся
        String wrongAnswerMessage = "Друг подумай еще разок над тем что сказал";
        ResponseContainer responseContainer = new ResponseContainer();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(wrongAnswerMessage);
        responseContainer.setSendMessage(sendMessage);
        responseContainer.setBotName("Mayak"); //дежурный по стране
        responseContainer.setTimingOfReply(100);
        return responseContainer;
    }

    public synchronized ResponseContainer configureMessage(Reply reply, Message message) {
        String chatId = message.getChatId().toString();
        ResponseContainer responseContainer = new ResponseContainer();
        responseContainer.setTimingOfReply(reply.getTiming());
        responseContainer.setBotName(reply.getBotName());


        if (reply.hasPicture()) {
            Long pictureId = reply.getPictureId();
            String pathToPicture = pictureService.getPathToPicture(pictureId);
            File fileFromDb = new File(pathToPicture);
            InputFile pictureFile = new InputFile(fileFromDb);

            SendPhoto sendPhoto = new SendPhoto();
            sendPhoto.setChatId(chatId);
            sendPhoto.setPhoto(pictureFile);

            responseContainer.setSendPhoto(sendPhoto);
        }
        if (reply.hasAudio()) {
            Long audioId = reply.getAudioId();
            String pathToAudio = audioService.getPathToAudio(audioId);
            File fileFromDb = new File(pathToAudio);
            InputFile audioFile = new InputFile(fileFromDb);

            SendVoice sendVoice = new SendVoice();
            sendVoice.setChatId(chatId);
            sendVoice.setVoice(audioFile);
            responseContainer.setSendVoice(sendVoice);
        }
        if (reply.hasGeolocation()) {
            Long geolocationId = reply.getGeolocationId();
            Geolocation geolocation = geolocationDatabaseService.getGeolocationById(geolocationId);
            Double latitudeToSend = geolocation.getLatitude();
            Double longitudeToSend = geolocation.getLongitude();

            SendLocation sendLocation = new SendLocation();
            sendLocation.setChatId(chatId);
            sendLocation.setLatitude(latitudeToSend);
            sendLocation.setLongitude(longitudeToSend);

            responseContainer.setSendLocation(sendLocation);
        }
        if (reply.hasText()) {
            String textToSend = reply.getTextMessage();

            SendMessage sendMessage = new SendMessage();
            sendMessage.setText(textToSend);

            responseContainer.setSendMessage(sendMessage);
        }
        return responseContainer;
    }

}

