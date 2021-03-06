package ru.kumkuat.application.GameModule.Service;

import com.vdurmont.emoji.EmojiParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.*;
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
    private final StickerService stickerService;

    public ResponseService(SceneService sceneService, TelegramChatService telegramChatService, PictureService pictureService,
                           AudioService audioService, BotController botController,
                           GeolocationDatabaseService geolocationDatabaseService,
                           TriggerService triggerService, UserService userService, StickerService stickerService) {
        this.sceneService = sceneService;
        this.telegramChatService = telegramChatService;
        this.pictureService = pictureService;
        this.audioService = audioService;
        this.botController = botController;
        this.geolocationDatabaseService = geolocationDatabaseService;
        this.triggerService = triggerService;

        this.userService = userService;
        this.stickerService = stickerService;
    }

    public void messageReceiver(Message message, boolean isNavigationCommand) {
        Long userId = Long.valueOf(message.getFrom().getId());
        if (userService.IsUserExist(userId)) {
            Long sceneId = getSceneId(message.getFrom().getId());
            Scene scene = sceneService.getScene(sceneId);
            Trigger sceneTrigger = scene.getTrigger();
            String chatId = message.getChatId().toString();
            try {
                if (isNavigationCommand) {    //сделал отдельно чтобы не проводить проверку checkIncomingMessage лишний раз
                    ReplyResolver(chatId, scene, userId, message);
                    userService.incrementSceneId(userId);
                } else {
                    if (checkIncomingMessage(message, sceneTrigger)) {
                        ReceiveNextReplies(chatId, userId, message);
                    } else {
                        ResponseContainer wrongAnswerResponse = configureWrongTriggerMessage(chatId);
                        botController.responseResolver(wrongAnswerResponse);
                    }
                }
//            } catch (IncomingMessageException exception) {
//                exception.printStackTrace();
//                log.debug("Incoming message Exception!");}
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void ReceiveNextReplies(String chatId, Long userTelegramId, Message message) {
        try {
            Scene scene = sceneService.getScene(getSceneId(userTelegramId.intValue()));
            User user = userService.getUser(userTelegramId);
            ReplyResolver(chatId, scene, userTelegramId, message);
            if (user.getSceneId() >= sceneService.count() - 1 && !user.isAdmin()) {
                Thread.sleep(300000);
                kickAllCommand.KickChatMember(marshakBot, telegramChatService.getChatByUserTelegramId(user.getTelegramUserId()));
            }
            userService.incrementSceneId(userTelegramId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean checkIncomingMessage(Message message, Trigger trigger) {
        boolean result;
        if (!isUserTriggered(message)) {
            result = checkTriggerOfIncomingMessage(message, trigger);
        } else {
            result = false;
        }
        return result;

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
                // result = triggerService.triggerCheck(sceneTrigger, userText);
            }
        }
        if (message.hasLocation()) {
            Location userLocation = message.getLocation();
            result = triggerService.triggerCheck(sceneTrigger, userLocation);
        }
        if (result) {
            triggUser(message);
        }
        return result;
    }

    private void ReplyResolver(String chatId, Scene scene, Long userId, Message message) {
        List<Reply> replyList = scene.getReplyCollection();
        ResponseContainer responseContainer;
        for (Reply reply : replyList) {
            responseContainer = configureMessage(reply, chatId, userId);
            responseContainer.setMessage(message);
            botController.responseResolver(responseContainer);
        }
        userService.setUserTrigger(userId, false);
    }


    private ResponseContainer configureWrongTriggerMessage(String chatId) {
        String wrongAnswerMessage = "Мне кажется, я вас не совсем понимаю.";
        ResponseContainer responseContainer = new ResponseContainer();
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(wrongAnswerMessage);
        sendMessage.setChatId(chatId);
        responseContainer.setSendMessage(sendMessage);
        responseContainer.setBotName("Mayakovsky"); //дежурный по стране
        responseContainer.setTimingOfReply(100);
        return responseContainer;
    }


    private boolean isUserTriggered(Message message) {
        Long userId = Long.valueOf(message.getFrom().getId());
        User user = userService.getUser(userId);
        return user.isTriggered();
    }

    private void triggUser(Message message) {
        Long userId = Long.valueOf(message.getFrom().getId());
        User user = userService.getUser(userId);
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
            String name = userService.getUser(userId).getNickName();
            result = text.replace("@ИмяЗрителя", name);
        }
        return result;
    }


}

