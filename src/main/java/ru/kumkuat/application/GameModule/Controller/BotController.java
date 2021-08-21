package ru.kumkuat.application.GameModule.Controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.kumkuat.application.GameModule.Abstract.TelegramWebhookCommandBot;
import ru.kumkuat.application.GameModule.Bot.*;
import ru.kumkuat.application.GameModule.Collections.ResponseContainer;
import ru.kumkuat.application.GameModule.Models.User;
import ru.kumkuat.application.GameModule.Service.ResponseService;
import ru.kumkuat.application.GameModule.Service.TelegramChatService;
import ru.kumkuat.application.GameModule.Service.UpdateValidationService;
import ru.kumkuat.application.GameModule.Service.UserService;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class BotController {

    private final List<TelegramWebhookBot> botCollection;
    private final ResponseService responseService;
    private final UserService userService;
    private final TelegramChatService telegramChatService;
    @Autowired
    private UpdateValidationService updateValidationService;

    public BotController(MarshakBot marshakBot, Harms harms, MayakBot mayakBot, AkhmatovaBot akhmatovaBot,
                         Brodskiy brodskiy, ResponseService responseService, UserService userService, TelegramChatService telegramChatService) {
        this.responseService = responseService;
        this.userService = userService;
        this.telegramChatService = telegramChatService;

        botCollection = new ArrayList<>();
        botCollection.add(harms);
        botCollection.add(mayakBot);
        botCollection.add(akhmatovaBot);
        botCollection.add(brodskiy);
        botCollection.add(marshakBot);

        webhookSetting(brodskiy);
        webhookSetting(marshakBot);
    }

    //beanPreconstruct??? or to bots
    private void webhookSetting(TelegramWebhookBot telegramWebhookBot) {
        try {
            SetWebhook setWebhook = new SetWebhook();
            setWebhook.setUrl(telegramWebhookBot.getBotPath());
            telegramWebhookBot.execute(setWebhook);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void resolveUpdatesFromSimpleListener(Message updateMessage) {
        updateValidationService.registerUser(updateMessage.getFrom());
        User user = userService.getUserByTelegramId(updateMessage.getFrom().getId().longValue());
        if (!user.isAdmin()
                && !updateMessage.getChat().getType().equals("private")
                && !commandChecker(updateMessage)) {
            Thread myThready = new Thread(new CallBotResponse(updateMessage));
            myThready.start();
        }
    }

    public void resolveUpdatesFromAdminListener(Message updateMessage) {
        updateValidationService.registerUser(updateMessage.getFrom());
        if (updateMessage.getChat().getType().equals("private")) {
            if (commandChecker(updateMessage)) {
                //Тут должно быть исполнение команд
            } else {
                User user = userService.getUserByTelegramId(updateMessage.getFrom().getId().longValue());
                if ((user.isPlaying() &&
                        !telegramChatService.isUserAlreadyGetChat(user.getTelegramUserId()))) {
                    Thread myThready = new Thread(new CallBotResponse(updateMessage));
                    myThready.start();
                }
            }
        }
    }

    public void responseResolver(List<ResponseContainer> responseContainers) {
        if (!responseContainers.isEmpty()) {
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
                    var marshak = botCollection.stream().filter(bot -> bot.getBotUsername().equals(botName)).findFirst();
                    if (!marshak.isEmpty()) {
                        sendResponseToUserInPrivate(responseContainer, (TelegramWebhookCommandBot) marshak.get());
                        log.debug("BotController processed reply of {}.", "Marshak");
                    }
                } else {
                    var botReplier = botCollection.stream().filter(bot -> bot.getBotUsername().equals(botName)).findFirst();
                    if (!botReplier.isEmpty()) {
                        sendResponseToUser(responseContainer, (BotsSender) botReplier.get());
                        log.debug("BotController processed reply of {}.", botName);
                    }
                }
            }
            if (!wrongMessage) {
                userService.incrementSceneId(userId);
                userService.setUserTrigger(userId, false);
            }
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
                //Не понял зачем это в аргументы пихать
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
            if (incomingMessage.hasText() && commandChecker(incomingMessage)) {
                log.debug("Received throw to Marshak.");
                responseResolver(responseService.messageReceiver(incomingMessage));
            } else {
                log.debug("Received message.");
                responseResolver(responseService.messageReceiver(incomingMessage));
            }
        }
    }

    private boolean commandChecker(Message message) {
        return botCollection.stream().filter(bot -> bot instanceof TelegramWebhookCommandBot)
                .anyMatch(bot -> ((TelegramWebhookCommandBot) bot).isCommand(message.getText()));
    }
}

