package ru.kumkuat.application.GameModule.Service;

import com.vdurmont.emoji.EmojiParser;
import lombok.extern.slf4j.Slf4j;
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
import ru.kumkuat.application.GameModule.Exceptions.IncomingMessageException;
import ru.kumkuat.application.GameModule.Models.Geolocation;
import ru.kumkuat.application.GameModule.Models.User;
import ru.kumkuat.application.TemporaryCollections.SceneCollection;

import java.io.File;
import java.util.List;

@Slf4j
@Service
public class ResponseService {

    private final SceneCollection sceneCollection;
    private final PictureService pictureService;
    private final AudioService audioService;
    private final BotController botController;
    private final GeolocationDatabaseService geolocationDatabaseService;
    private final TriggerService triggerService;
    private final UserService userService;

    public ResponseService(SceneCollection sceneCollection, PictureService pictureService,
                           AudioService audioService, BotController botController,
                           GeolocationDatabaseService geolocationDatabaseService,
                           TriggerService triggerService, UserService userService) {
        this.sceneCollection = sceneCollection;
        this.pictureService = pictureService;
        this.audioService = audioService;
        this.botController = botController;
        this.geolocationDatabaseService = geolocationDatabaseService;
        this.triggerService = triggerService;

        this.userService = userService;
    }


    private boolean checkIncomingMessage(Message message, Trigger sceneTrigger) throws IncomingMessageException {

        if (message.hasText()) {
            String userText = message.getText();

            return triggerService.triggerCheck(sceneTrigger, userText);
        }
        if (message.hasPhoto()) {
            return sceneTrigger.isHasPicture();
        }

        if (message.hasLocation()) {
            Location userLocation = message.getLocation();
            return triggerService.triggerCheck(sceneTrigger, userLocation);
        }
        throw new IncomingMessageException("checkIncomingMessage didn't happened!");
    }

    public void messageReceiver(Message message) {
        Long userId = Long.valueOf(message.getFrom().getId());
        if (userService.IsUserExist(userId)) {
            Long sceneId = getSceneId(message);
            Scene scene = sceneCollection.get(sceneId);
            Trigger sceneTrigger = scene.getTrigger();
            try {
                if (checkIncomingMessage(message, sceneTrigger)) {
                    ReplyResolver(message, scene);
                    userService.incrementSceneId(userId);
                } else {
                    ResponseContainer wrongAnswerResponse = configureWrongTriggerMessage(message, scene);
                    botController.responseResolver(wrongAnswerResponse);
                }
            } catch (IncomingMessageException exception) {
                exception.printStackTrace();
                log.debug("Incoming message Exception!");
            }
        }
    }

    private void ReplyResolver(Message message, Scene scene) {
        List<Reply> replyList = scene.getReplyCollection();
        ResponseContainer responseContainer;
        for (Reply reply : replyList) {
            responseContainer = configureMessage(reply, message);
            botController.responseResolver(responseContainer);
        }
    }

    private ResponseContainer configureWrongTriggerMessage(Message message, Scene scene) {
        //пока Message и Scene не используются но при реализации индивидуальных ответов для каждого триггера они понадобятся
        String wrongAnswerMessage = "Друг подумай еще разок над тем что сказал";
        ResponseContainer responseContainer = new ResponseContainer();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(wrongAnswerMessage);
        sendMessage.setChatId(message.getChatId().toString());
        responseContainer.setSendMessage(sendMessage);
        responseContainer.setBotName("Mayakovsky"); //дежурный по стране
        responseContainer.setTimingOfReply(100);
        return responseContainer;
    }

    private synchronized ResponseContainer configureMessage(Reply reply, Message message) {
        String chatId = message.getChatId().toString();
        ResponseContainer responseContainer = new ResponseContainer();
        responseContainer.setTimingOfReply(reply.getTiming());
        responseContainer.setBotName(reply.getBotName());


        if (reply.hasPicture()) {
            log.debug("Reply has picture.");
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
            log.debug("Reply has audio.");
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
            log.debug("Reply has geolocation.");
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
            log.debug("Reply has text.");
            String textToSend = reply.getTextMessage();
            textToSend = EmojiParser.parseToUnicode(textToSend);
            SendMessage sendMessage = new SendMessage();
            sendMessage.setText(textToSend);
            sendMessage.setChatId(chatId);
            responseContainer.setSendMessage(sendMessage);
        }
        log.debug("Response container created.");
        return responseContainer;
    }

    private Long getSceneId(Message message) throws NullPointerException {
        Long userId = Long.valueOf(message.getFrom().getId());
        User user = null;
        try {
            user = userService.getUser(userId);
        } catch (NullPointerException e) {
            e.getMessage();
            log.debug("User is null. Is absent in DB");
        }
        if (user == null) {
            throw new NullPointerException("User is null.");
        }

        return user.getSceneId();
    }
}

