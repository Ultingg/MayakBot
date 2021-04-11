package ru.kumkuat.application.GameModule.Service;

import com.vdurmont.emoji.EmojiParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendLocation;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendVoice;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Location;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.kumkuat.application.GameModule.Bot.MarshakBot;
import ru.kumkuat.application.GameModule.Collections.Reply;
import ru.kumkuat.application.GameModule.Collections.ResponseContainer;
import ru.kumkuat.application.GameModule.Collections.Scene;
import ru.kumkuat.application.GameModule.Collections.Trigger;
import ru.kumkuat.application.GameModule.Commands.MarshakCommands.KickAllCommand;
import ru.kumkuat.application.GameModule.Controller.BotController;
import ru.kumkuat.application.GameModule.Exceptions.IncomingMessageException;
import ru.kumkuat.application.GameModule.Models.Geolocation;
import ru.kumkuat.application.GameModule.Models.User;

import java.io.File;
import java.util.List;

@Slf4j
@Service
public class ResponseService {

    @Autowired
    private MarshakBot marshakBot;
    @Autowired
    private KickAllCommand kickAllCommand;

    private final SceneService sceneService;
    private final TelegramChatService telegramChatService;
    private final PictureService pictureService;
    private final AudioService audioService;
    private final BotController botController;
    private final GeolocationDatabaseService geolocationDatabaseService;
    private final TriggerService triggerService;
    private final UserService userService;

    public ResponseService(SceneService sceneService, TelegramChatService telegramChatService, PictureService pictureService,
                           AudioService audioService, BotController botController,
                           GeolocationDatabaseService geolocationDatabaseService,
                           TriggerService triggerService, UserService userService) {
        this.sceneService = sceneService;
        this.telegramChatService = telegramChatService;
        this.pictureService = pictureService;
        this.audioService = audioService;
        this.botController = botController;
        this.geolocationDatabaseService = geolocationDatabaseService;
        this.triggerService = triggerService;

        this.userService = userService;
    }

    public void messageReceiver(Message message, boolean isNavigationCommand) {
        Long userId = Long.valueOf(message.getFrom().getId());
        if (userService.IsUserExist(userId)) {
            Long sceneId = getSceneId(message);
            Scene scene = sceneService.getScene(sceneId);
            Trigger sceneTrigger = scene.getTrigger();
            String chatId = message.getChatId().toString();
            try {
                if (isNavigationCommand) {    //сделал отдельно чтобы не проводить проверку checkIncomingMessage лишний раз
                    ReplyResolver(chatId, scene);
                    userService.incrementSceneId(userId);
                } else {
                    if (checkIncomingMessage(message, sceneTrigger)) {
                        ReplyResolver(chatId, scene);
                        var user = userService.getUser(userId);
                        if (user.getSceneId() >= sceneService.count() - 1) {
                            kickAllCommand.kickChatMember(marshakBot, telegramChatService.getChatByUserTelegramId(user.getTelegramUserId()));
                        }
                        userService.incrementSceneId(userId);
                    } else {
                        ResponseContainer wrongAnswerResponse = configureWrongTriggerMessage(chatId);
                        botController.responseResolver(wrongAnswerResponse);
                    }
                }
            } catch (IncomingMessageException exception) {
                exception.printStackTrace();
                log.debug("Incoming message Exception!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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


    private void ReplyResolver(String chatId, Scene scene) {
        List<Reply> replyList = scene.getReplyCollection();
        ResponseContainer responseContainer;
        for (Reply reply : replyList) {
            responseContainer = configureMessage(reply, chatId);
            botController.responseResolver(responseContainer);
        }
    }

    private ResponseContainer configureWrongTriggerMessage(String chatId) {
        String wrongAnswerMessage = "Друг подумай еще разок над тем что сказал";
        ResponseContainer responseContainer = new ResponseContainer();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(wrongAnswerMessage);
        sendMessage.setChatId(chatId);
        responseContainer.setSendMessage(sendMessage);
        responseContainer.setBotName("Mayakovsky"); //дежурный по стране
        responseContainer.setTimingOfReply(100);
        return responseContainer;
    }

    private synchronized ResponseContainer configureMessage(Reply reply, String chatId) {
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

