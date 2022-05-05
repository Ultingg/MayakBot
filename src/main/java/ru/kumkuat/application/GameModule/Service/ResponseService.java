package ru.kumkuat.application.GameModule.Service;

import com.vdurmont.emoji.EmojiParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.*;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Location;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.kumkuat.application.GameModule.Collections.Reply;
import ru.kumkuat.application.GameModule.Collections.ResponseContainer;
import ru.kumkuat.application.GameModule.Collections.Scene;
import ru.kumkuat.application.GameModule.Collections.Trigger;
import ru.kumkuat.application.GameModule.Models.Geolocation;
import ru.kumkuat.application.GameModule.Models.User;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class ResponseService {
    private final SceneService sceneService;
    private final PictureService pictureService;
    private final AudioService audioService;
    private final GeolocationDatabaseService geolocationDatabaseService;
    private final TriggerService triggerService;
    private final UserService userService;
    private final StickerService stickerService;

    public ResponseService(SceneService sceneService, PictureService pictureService,
                           AudioService audioService,
                           GeolocationDatabaseService geolocationDatabaseService,
                           TriggerService triggerService, UserService userService, StickerService stickerService) {
        this.sceneService = sceneService;
        this.pictureService = pictureService;
        this.audioService = audioService;
        this.geolocationDatabaseService = geolocationDatabaseService;
        this.triggerService = triggerService;
        this.userService = userService;
        this.stickerService = stickerService;
    }

    public List<ResponseContainer> messageReceiver(Message message) {
        List<ResponseContainer> responseContainers = new ArrayList<>();
        Long userId = Long.valueOf(message.getFrom().getId());
        User user = userService.getUserByTelegramId(userId);
        Long sceneId = getSceneId(message.getFrom().getId());
        log.info("Resolving message...");
        if (user.getSceneId() <= sceneService.count() - 1) {
            Scene scene = sceneService.getScene(sceneId);
            Trigger sceneTrigger = scene.getTrigger();
            String chatId = message.getChatId().toString();
            log.info("Resolving message..chatid; {}  checking message...userId: {}",chatId, userId);
                if (checkIncomingMessage(message, sceneTrigger)) {
                    log.info("Collectin replies for user {} in chat {}", userId, chatId);
                    responseContainers = ReceiveNextReplies(chatId, userId, message);                 //вернуть такой ответ
                } else {
                    if (!isTypeIncommingMessageEqualTriggerType(message, sceneTrigger)) {
                        log.info("Add new message to list of wrong messages.");
                        responseContainers = List.of(configureWrongTriggerMessage(chatId, userId));
                    }
                }
            log.info("ResponseContainers size: {}", responseContainers.size());
        }
        return responseContainers;
    }

    private boolean isTypeIncommingMessageEqualTriggerType(Message message, Trigger sceneTrigger) {
        if (isUserNotTriggered(message)) {
            if (message.hasPhoto()) {
                log.info("message has photo");
                return sceneTrigger.isHasPicture();
            }
            if (message.hasText()) {
                log.info("message has text");
                log.info("scenTriger loation: {} , pictuer: {} ",sceneTrigger.isHasGeolocation(), sceneTrigger.isHasPicture());
                return !sceneTrigger.isHasGeolocation() && !sceneTrigger.isHasPicture();
            }
            if (message.hasLocation()) {
                log.info("message has location");
                return sceneTrigger.isHasGeolocation();
            }
        }
        return true;
    }

    public List<ResponseContainer> ReceiveNextReplies(String chatId, Long userTelegramId, Message message) {
        log.info("Reciveing message started...");
        Scene scene = sceneService.getScene(getSceneId(userTelegramId.intValue()));
        return replyResolver(chatId, scene, userTelegramId, message);
    }

    private boolean checkIncomingMessage(Message message, Trigger trigger) {
        return isUserNotTriggered(message) && checkTriggerOfIncomingMessage(message, trigger);
    }

    private boolean checkTriggerOfIncomingMessage(Message message, Trigger sceneTrigger) {
        boolean result = false;
        if (message.hasPhoto()) {
            result = sceneTrigger.isHasPicture();
        }
        if (message.hasText()) {
            if (checkForNickNameSetting(sceneTrigger)) {
                result = nickNameSetter(message);
            } else if (sceneTrigger.isHasPicture()) {
                result = false;
            } else {
                result = true;
            }
        }
        if (message.hasLocation()) {
            Location userLocation = message.getLocation();
            result = triggerService.triggerCheck(sceneTrigger, userLocation);
        }
        if (result) {
            triggUser(message);
        }
        log.info("Message checker boolean : {}", result);
        return result;
    }

    private List<ResponseContainer> replyResolver(String chatId, Scene scene, Long userId, Message message) {  // отправить список контейнеров
        List<Reply> replyList = scene.getReplyCollection();
        List<ResponseContainer> responseContainers = new ArrayList<>();
        ResponseContainer responseContainer;
        log.info("Configuring replies...");
        for (Reply reply : replyList) {
            responseContainer = configureMessage(reply, chatId, userId);
            responseContainer.setMessage(message);
            responseContainers.add(responseContainer);
        }
        log.info("Configuration of replies done. There are {} replies in container.", responseContainers.size());
        return responseContainers;
    }


    private ResponseContainer configureWrongTriggerMessage(String chatId, Long userId) {
        String wrongAnswerMessage = "Мне кажется, я вас не совсем понимаю.";
        ResponseContainer responseContainer = new ResponseContainer();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(wrongAnswerMessage);
        sendMessage.setChatId(chatId);
        responseContainer.setSendMessage(sendMessage);
        responseContainer.setBotName("Mayakovsky"); //дежурный по стране
        responseContainer.setTimingOfReply(100);
        responseContainer.setWrongMessage(true);
        responseContainer.setUserId(userId);
        return responseContainer;
    }

    private boolean isUserNotTriggered(Message message) {
        Long userId = Long.valueOf(message.getFrom().getId());
        User user = userService.getUserByTelegramId(userId);
        log.info("User {} trigger is {}", userId, user.isTriggered());
        return !user.isTriggered();
    }

    private void triggUser(Message message) {
        Long userId = Long.valueOf(message.getFrom().getId());
        User user = userService.getUserByTelegramId(userId);
        user.setTriggered(true);
        userService.setUserTrigger(userId, true);
    }

    private synchronized ResponseContainer configureMessage(Reply reply, String chatId, Long userId) {
        ResponseContainer responseContainer = new ResponseContainer();
        responseContainer.setTimingOfReply(reply.getTiming());
        responseContainer.setBotName(reply.getBotName());
        responseContainer.setUserId(userId);

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
            textToSend = nickNameInserting(textToSend, Long.valueOf(userId));
            SendMessage sendMessage = new SendMessage();
            sendMessage.setText(textToSend);
            sendMessage.setChatId(chatId);
            responseContainer.setSendMessage(sendMessage);
        }
        if (reply.hasSticker()) {
            log.debug("Reply has sticker.");
            Long stickerId = reply.getStickerId();
            String pathToSticker = stickerService.getPathToSticker(stickerId);
            File fileFromDb = new File(pathToSticker);
            InputFile stickerFile = new InputFile(fileFromDb);

            SendSticker sendSticker = new SendSticker();
            sendSticker.setSticker(stickerFile);
            sendSticker.setChatId(chatId);
            responseContainer.setSendSticker(sendSticker);
        }
        log.debug("Response container created.");
        return responseContainer;
    }

    private Long getSceneId(Integer userTelegeramId) throws NullPointerException {
        Long userId = Long.valueOf(userTelegeramId);
        User user = null;
        try {
            user = userService.getUserByTelegramId(userId);
        } catch (NullPointerException e) {
            e.getMessage();
            log.debug("User is null. Is absent in DB");
        }
        if (user == null) {
            throw new NullPointerException("User is null.");
        }

        return user.getSceneId();
    }

    private boolean nickNameSetter(Message message) {
        String nickName = message.getText();
        userService.setUserNickName(message.getFrom().getId().longValue(), nickName);
        return true;
    }

    private boolean checkForNickNameSetting(Trigger trigger) {
        String text = trigger.getText();
        return text != null && text.equals("имя");
    }

    private String nickNameInserting(String text, Long userId) {
        String result = text;
        if (text.contains("@ИмяЗрителя")) {
            String name = userService.getUserByTelegramId(userId).getNickName();
            result = text.replace("@ИмяЗрителя", name);
        }
        return result;
    }


}

