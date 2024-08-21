package ru.kumkuat.application.gameModule.cuncurrency.executor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.kumkuat.application.gameModule.Abstract.TelegramWebhookCommandBot;
import ru.kumkuat.application.gameModule.bot.BotsSender;
import ru.kumkuat.application.gameModule.collections.PinnedMessageDTO;
import ru.kumkuat.application.gameModule.collections.ResponseContainer;
import ru.kumkuat.application.gameModule.service.UserService;

/**
 * Логика обработки контейнера сообщения
 */
@Slf4j
@Service
public class SimpleUpdateExecutor extends Executor {
    private final static String MARSHAK = "Marshak";
    private final UserService userService;

    public SimpleUpdateExecutor(UserService userService) {
        this.userService = userService;
    }

    public void execute(ResponseContainer update, BotsSender bot) throws InterruptedException {
        log.info("Обработка update началась. Время: " + update.getTimingOfReply());
        String botName = bot.getSecretName();
        if (botName.equals(MARSHAK)) {
            sendResponseToUserInPrivate(update, (TelegramWebhookCommandBot) bot);
        } else {
            sendResponseToUser(update, bot);
        }

        if (update.isLastMessage() && !update.isWrongMessage()) {
            Long userId = update.getUserId();
            userService.moveUserToNextScene(userId);
        }
        System.out.println("====================== " + update.getMessage().getText() + " ======================");
        log.info("Обработка update закончилась");
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
        if (responseContainer.hasPinnedMessage()) {
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
        if (responseContainer.hasPinnedMessage()) {
            PinnedMessageDTO pinnedMessageDTO = responseContainer.getPinnedMessageDTO();
            botsSender.sendPinnedMessage(pinnedMessageDTO);
        }
    }

}
