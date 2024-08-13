package ru.kumkuat.application.gameModule.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.pinnedmessages.PinChatMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.kumkuat.application.gameModule.Abstract.TelegramWebhookCommandBot;
import ru.kumkuat.application.gameModule.bot.*;
import ru.kumkuat.application.gameModule.collections.PinnedMessageDTO;
import ru.kumkuat.application.gameModule.collections.ResponseContainer;
import ru.kumkuat.application.gameModule.models.User;
import ru.kumkuat.application.gameModule.promocode.Service.PromocodeLogeService;
import ru.kumkuat.application.gameModule.promocode.Service.PromocodeService;
import ru.kumkuat.application.gameModule.service.ResponseService;
import ru.kumkuat.application.gameModule.service.TelegramChatService;
import ru.kumkuat.application.gameModule.service.UpdateValidationService;
import ru.kumkuat.application.gameModule.service.UserService;

import java.util.ArrayList;
import java.util.List;

@Component
public class BotController {

    Logger log = LoggerFactory.getLogger(BotController.class.getName());

    private final List<BotsSender> botCollection;
    private final ResponseService responseService;
    private final UserService userService;
    private final TelegramChatService telegramChatService;
    private final PromocodeLogeService promocodeLogeService;
    private final PromocodeService promocodeService;
    @Autowired
    private UpdateValidationService updateValidationService;

    public BotController(MarshakBot marshakBot, Harms harms, MayakBot mayakBot, AkhmatovaBot akhmatovaBot,
                         Brodskiy brodskiy, ResponseService responseService, UserService userService, TelegramChatService telegramChatService,
                         PromocodeLogeService promocodeLogeService, PromocodeService promocodeService) {
        this.responseService = responseService;
        this.userService = userService;
        this.telegramChatService = telegramChatService;
        this.promocodeLogeService = promocodeLogeService;
        this.promocodeService = promocodeService;

        botCollection = new ArrayList<>();
        botCollection.add(harms);
        botCollection.add(mayakBot);
        botCollection.add(akhmatovaBot);
        botCollection.add(brodskiy);
        botCollection.add(marshakBot);

        webhookSetting(brodskiy);
        webhookSetting(marshakBot);
    }

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
        long userId = userService.getCheckedUserId(updateMessage);
        log.info("user id from update: {}", userId);
        User user = userService.getUserByTelegramId(userId);
        if (user != null && !user.isAdmin()
                && !updateMessage.getChat().getType().equals("private")
                && !commandChecker(updateMessage)) {
            Thread myThready = new Thread(new CallBotResponse(updateMessage));
            myThready.start();
        }
    }

    public void resolveUpdatesFromAdminListener(Message updateMessage) {
        updateValidationService.registerUser(updateMessage.getFrom());
        if (updateMessage.hasText()
                && (updateMessage.getChat().getType().equals("private"))) {
            promoResolve(updateMessage);
            User user = userService.getUserByTelegramId(updateMessage.getFrom().getId().longValue());
            if ((user.isPlaying() && !telegramChatService.isUserAlreadyGetChat(user.getTelegramUserId()))) {
                Thread myThready = new Thread(new CallBotResponse(updateMessage));
                myThready.start();
            }
        }
    }

    public void resolveSuccessfulPayment(Update update) {
        var marshak = (MarshakBot) botCollection.stream().filter(bot -> bot instanceof MarshakBot).findFirst().get();
        marshak.onWebhookUpdateReceived(update);
    }

    public void resolveCommandMessage(Update update) {
        Message updateMessage = update.getMessage();
        User user = userService.getUserByTelegramId(updateMessage.getFrom().getId());
        updateValidationService.registerUser(updateMessage.getFrom());
        if (updateMessage.getChat().getType().equals("private") || (user != null && user.isAdmin())) {
            var marshak = (MarshakBot) botCollection.stream().filter(bot -> bot instanceof MarshakBot).findFirst().get();
            marshak.onWebhookUpdateReceived(update);
        }
    }

    public void resolvePerCheckoutQuery(Update update) {
        TelegramWebhookCommandBot marshak = (MarshakBot) botCollection.stream().filter(bot -> bot instanceof MarshakBot).findFirst().get();
        marshak.onWebhookUpdateReceived(update);
    }

    public void resolveCallbackQueryFromAdminListener(Update update) {
        var marshak = (MarshakBot) botCollection.stream().filter(bot -> bot instanceof MarshakBot).findFirst().get();
        var user = update.getCallbackQuery().getMessage().getFrom();
        user.setId(user.getId().equals(marshak.getId()) ? update.getCallbackQuery().getMessage().getChatId() : user.getId());
        updateValidationService.registerUser(update.getCallbackQuery().getMessage().getFrom());
        marshak.onWebhookUpdateReceived(update);
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
                    var marshak = botCollection.stream().filter(bot -> bot.getSecretName().equals(botName)).findFirst();
                    if (marshak.isPresent()) {
                        sendResponseToUserInPrivate(responseContainer, (TelegramWebhookCommandBot) marshak.get());
                        log.debug("BotController processed reply of {}.", "Marshak");
                    }
                } else {
                    var botReplier = botCollection.stream().filter(bot -> bot.getSecretName().equals(botName)).findFirst();
                    if (botReplier.isPresent()) {
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
        if(responseContainer.hasPinnedMessage()){
            PinnedMessageDTO pinnedMessageDTO = responseContainer.getPinnedMessageDTO();
            botsSender.sendPinnedMessage(pinnedMessageDTO);
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
                command.processMessage(telegramWebhookBot, message, arguments);
            } else {
//                responseContainer.getSendMessage().setChatId(responseContainer.getUserId().toString());
                botsSender.sendMessage(responseContainer.getSendMessage());
            }
        }
        if (responseContainer.hasSticker()) {
            var sendSticker = responseContainer.getSendSticker();
            sendSticker.setChatId(responseContainer.getUserId().toString());
            botsSender.sendSticker(sendSticker);
        }
        if(responseContainer.hasPinnedMessage()){
            PinnedMessageDTO pinnedMessageDTO = responseContainer.getPinnedMessageDTO();
            botsSender.sendPinnedMessage(pinnedMessageDTO);
        }
    }

    class CallBotResponse implements Runnable {
        Message message;

        CallBotResponse(Message message) {
            this.message = message;
        }

        @Override
        public void run() {
            log.info("Another thread launched!");
            Message incomingMessage = message;
            if (incomingMessage.hasText() && commandChecker(incomingMessage)) {
                log.info("Received throw to Marshak.");
                responseResolver(responseService.messageReceiver(incomingMessage));
            } else {
                log.info("Received message.");
                responseResolver(responseService.messageReceiver(incomingMessage));
            }
        }
    }

    private boolean commandChecker(Message message) {
        return botCollection.stream().filter(bot -> bot instanceof TelegramWebhookCommandBot)
                .anyMatch(bot -> ((TelegramWebhookCommandBot) bot).isCommand(message.getText()));
    }

    private void commandExecute(Message message) {
        var commandBot = botCollection.stream().filter(bot -> bot instanceof TelegramWebhookCommandBot)
                .filter(bot -> ((TelegramWebhookCommandBot) bot).isCommand(message.getText())).findFirst();
        if (!commandBot.isEmpty()) {
            ((TelegramWebhookCommandBot) commandBot.get()).InvokeCommand(message);
        }
    }

    private void promoResolve(Message updateMessage) {
        User user = userService.getUserByTelegramId(updateMessage.getFrom().getId().longValue());
        var marshak = (MarshakBot) botCollection.stream().filter(bot -> bot instanceof MarshakBot).findFirst().get();

        String message = updateMessage.getText();
        if (message.equals(promocodeLogeService.getPromocode())) {
            log.info("User id: {} used promocode", user.getTelegramUserId());
            user.setPromo(true);
            userService.save(user);
            marshak.sendMessage(SendMessage.builder()
                    .chatId(updateMessage.getChatId().toString())
                    .text("Промокод принят").build());
        } else if (promocodeService.checkPromocode(message)) {
            log.info("User id: {} used FreePromocode", user.getTelegramUserId());
            user.setHasPay(true);
            userService.save(user);
            marshak.sendMessage(SendMessage.builder()
                    .chatId(updateMessage.getChatId().toString())
                    .text("Промокод принят. Вы можете бесплатно пройти по маршруту! Нажмите \"Начать прогулку\".").build());
        }
    }


    public void tetst(Update update) {
        Long chatId = update.getMessage().getChatId();

        SendMessage sendMessage = SendMessage.builder()
                .chatId(update.getMessage().getChatId().toString())
                .text("Это сообзение должно быть запиненно!").build();

        var marhsak =(MarshakBot) botCollection.stream().filter(bot -> bot.getSecretName().equals("Marshak")).findFirst().get();

        Integer messageId = marhsak.sendPrePinnedMessage(sendMessage);
        PinChatMessage pinChatMessage = PinChatMessage.builder()
                .chatId(chatId)
                .disableNotification(true)
                .messageId(messageId).build();

        marhsak.pinMesassge(pinChatMessage);

    }
}

