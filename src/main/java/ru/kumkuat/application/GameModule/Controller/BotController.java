package ru.kumkuat.application.GameModule.Controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.kumkuat.application.GameModule.Abstract.TelegramWebhookCommandBot;
import ru.kumkuat.application.GameModule.Bot.*;
import ru.kumkuat.application.GameModule.Collections.ResponseContainer;
import ru.kumkuat.application.GameModule.Models.User;
import ru.kumkuat.application.GameModule.Service.ResponseService;
import ru.kumkuat.application.GameModule.Service.TelegramChatService;
import ru.kumkuat.application.GameModule.Service.UserService;

import java.util.List;

@Slf4j
@Component
public class BotController {

    private final Harms harms;
    private final MayakBot mayakBot;
    private final AkhmatovaBot akhmatovaBot;
    private final Brodskiy brodskiy;
    private final MarshakBot marshakBot;
    private final ResponseService responseService;
    private final TelegramWebhookCommandBot telegramWebhookCommandBot;
    private final UserService userService;
    private final TelegramChatService telegramChatService;

    public BotController(MarshakBot marshakBot, Harms harms, MayakBot mayakBot, AkhmatovaBot akhmatovaBot,
                         Brodskiy brodskiy, ResponseService responseService, TelegramWebhookCommandBot telegramWebhookCommandBot, UserService userService, TelegramChatService telegramChatService) {
        this.harms = harms;
        this.mayakBot = mayakBot;
        this.akhmatovaBot = akhmatovaBot;
        this.brodskiy = brodskiy;
        this.marshakBot = marshakBot;
        this.responseService = responseService;
        this.telegramWebhookCommandBot = telegramWebhookCommandBot;
        this.userService = userService;
        this.telegramChatService = telegramChatService;

        webhookSetting();

    }
    //beanPreconstruct??? or to bots

    private void webhookSetting() {
        try {
            SetWebhook setWebhook = new SetWebhook();
            setWebhook.setUrl(brodskiy.getBotPath());
            brodskiy.execute(setWebhook);
            setWebhook.setUrl(marshakBot.getBotPath());
            marshakBot.execute(setWebhook);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }


//    public boolean IsBotsStarting(String UserId){
//        SendMessage checkMessage = new SendMessage();
//        checkMessage.setText("Проверка");
//        harms.execute();
//    }

    public void resolveUpdatesFromSimpleLIstner(Message updateMessage) {
        if (userService.IsUserExist(updateMessage.getFrom().getId().longValue()) &&
                !userService.getUserByTelegramId(updateMessage.getFrom().getId().longValue()).isAdmin()) {
            Thread myThready = new Thread(new CallBotResponse(updateMessage));

            myThready.start();
        }
    }

    public void resolveUpdatesFromAdminLIstner(Message updateMessage) {
        User user = null;
        //регистрация


        if (userService.IsUserExist(updateMessage.getFrom().getId().longValue())) {
            user = userService.getUserByTelegramId(updateMessage.getFrom().getId().longValue());

            if (user.isPlaying() && !telegramChatService.isUserAlreadyPlaying(user.getTelegramUserId())) {
                Thread myThready = new Thread(new CallBotResponse(updateMessage));
                myThready.start();
            }
        }
    }


    // удалить
//    public void responseResolver(ResponseContainer responseContainer) {
//        String botName = responseContainer.getBotName();
//        int time = responseContainer.getTimingOfReply();
//        try {
//            Thread.currentThread().sleep(time);
//        } // тут какая-то ахенея
//        catch (InterruptedException e) {
//            log.debug("Thread was Interrupted while waiting timing of reply.");
//            e.getStackTrace();
//        }
//        if (botName.equals("Marshak")) {
//            sendResponseToUserInPrivate(responseContainer, marshakBot);
//            log.debug("BotController processed reply of {}.", "Marshak");
//        } else {
//            if (botName.equals("Mayakovsky")) {
//                sendResponseToUser(responseContainer, mayakBot);
//                log.debug("BotController processed reply of {}.", "Myakovskiy");
//            }
//            if (botName.equals("Akhmatova")) {
//                sendResponseToUser(responseContainer, akhmatovaBot);
//                log.debug("BotController processed reply of {}.", "Akhmatova");
//            }
//            if (botName.equals("Brodskiy")) {
//                sendResponseToUser(responseContainer, brodskiy);
//                log.debug("BotController processed reply of {}.", "Brodskiy");
//            }
//            if (botName.equals("Harms")) {
//                sendResponseToUser(responseContainer, harms);
//                log.debug("BotController processed reply of {}.", "Harms");
//            }
//        }
//    }

    public void responseResolver(List<ResponseContainer> responseContainers) {
        boolean wrongMessage = false;
        long userId = responseContainers.get(0).getUserId();
        for (ResponseContainer responseContainer : responseContainers) {
            String botName = responseContainer.getBotName();
            wrongMessage = responseContainer.isWrongMessage();
            int time = responseContainer.getTimingOfReply();
            try {
                Thread.currentThread().sleep(time);
            } // тут какая-то ахенея
            catch (InterruptedException e) {
                log.debug("Thread was Interrupted while waiting timing of reply.");
                e.getStackTrace();
            }
            if (botName.equals("Marshak")) {
                sendResponseToUserInPrivate(responseContainer, marshakBot);
                log.debug("BotController processed reply of {}.", "Marshak");
            } else {
                if (botName.equals("Mayakovsky")) {
                    sendResponseToUser(responseContainer, mayakBot);
                    log.debug("BotController processed reply of {}.", "Myakovskiy");
                }
                if (botName.equals("Akhmatova")) {
                    sendResponseToUser(responseContainer, akhmatovaBot);
                    log.debug("BotController processed reply of {}.", "Akhmatova");
                }
                if (botName.equals("Brodskiy")) {
                    sendResponseToUser(responseContainer, brodskiy);
                    log.debug("BotController processed reply of {}.", "Brodskiy");
                }
                if (botName.equals("Harms")) {
                    sendResponseToUser(responseContainer, harms);
                    log.debug("BotController processed reply of {}.", "Harms");
                }
            }
        }
        if (!wrongMessage) {
            userService.incrementSceneId(userId);
            userService.setUserTrigger(userId, false);
        }
    }

    private void sendResponseToUser(ResponseContainer responseContainer, BotsSender botsSender) {

        if (responseContainer.hasGeolocation()) {
            botsSender.sendLocation(responseContainer.getSendLocation());
        }
        if (responseContainer.hasAudio()) {
            botsSender.sendVoice(responseContainer.getSendVoice());
        }
        if (responseContainer.hasPicture()) {
            botsSender.sendPicture(responseContainer.getSendPhoto());
        }
        if (responseContainer.hasText()) {
            botsSender.sendMessage(responseContainer.getSendMessage());
        }
        if (responseContainer.hasSticker()) {
            botsSender.sendSticker(responseContainer.getSendSticker());
        }
    }

    private void sendResponseToUserInPrivate(ResponseContainer responseContainer, TelegramWebhookCommandBot
            telegramWebhookBot) {
        var botsSender = (BotsSender) telegramWebhookBot;
        if (responseContainer.hasGeolocation()) {
            var sendLocation = responseContainer.getSendLocation();
            sendLocation.setChatId(responseContainer.getUserId().toString());
            botsSender.sendLocation(sendLocation);
        }
        if (responseContainer.hasAudio()) {
            var sendVoice = responseContainer.getSendVoice();
            sendVoice.setChatId(responseContainer.getUserId().toString());
            botsSender.sendVoice(sendVoice);
        }
        if (responseContainer.hasPicture()) {
            var sendPhoto = responseContainer.getSendPhoto();
            sendPhoto.setChatId(responseContainer.getUserId().toString());
            botsSender.sendPicture(sendPhoto);
        }
        if (responseContainer.hasText()) {
            if (telegramWebhookBot.isCommand(responseContainer.getSendMessage().getText())) {
                var command = telegramWebhookBot.getRegisteredCommand(responseContainer.getSendMessage().getText());
                String[] arguments = new String[]{responseContainer.getMessage().getText()};
                var message = responseContainer.getMessage();
                message.setText(responseContainer.getSendMessage().getText());
                command.processMessage(telegramWebhookBot, message, arguments);
            } else {
                botsSender.sendMessage(responseContainer.getSendMessage());
            }
        }
        if (responseContainer.hasSticker()) {
            var sendSticker = responseContainer.getSendSticker();
            sendSticker.setChatId(responseContainer.getUserId().toString());
            botsSender.sendSticker(sendSticker);
        }
    }

    class CallBotResponse implements Runnable {
        Message message;

        CallBotResponse(Message message) {
            this.message = message;
        }

        @Override
        public void run() {
            System.out.println("Привет из побочного потока!");
            Message incomingMessage = message;
            if (incomingMessage != null && commandChecker(incomingMessage)) {
                log.debug("Received throw to Marshak.");
                responseResolver(responseService.messageReceiver(incomingMessage, navigationCommandCheck(incomingMessage)));
            } else {
                log.debug("Received message.");
                responseResolver(responseService.messageReceiver(incomingMessage, false));
            }

        }
    }

    private boolean commandChecker(Message message) {
        return telegramWebhookCommandBot.isCommand(message.getText());
    }

    private boolean navigationCommandCheck(Message message) {
        boolean result = false;
        if (message.hasText()) {
            String textToCheck = message.getText();
            if (textToCheck.contains("/next") || textToCheck.contains("/previous")) {
                result = true;
            }
        }
        return result;
    }
}

