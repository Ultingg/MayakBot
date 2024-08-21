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
        log.info("Обработка update началась. Время: " + update.getTimingOfReply() + ". Cообщение для userId: " +  update.getUserId());
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
        log.info("Обработка update закончилась");
    }

    private void sendResponseToUser(ResponseContainer responseContainer, BotsSender botsSender) {

        if (responseContainer.hasText()) {
            botsSender.sendMessage(responseContainer.getSendMessage());
            return;
        }
        sendSimpleMessages(responseContainer, botsSender);
    }

    private void sendSimpleMessages(ResponseContainer responseContainer, BotsSender botsSender) {
        if (responseContainer.hasGeolocation()) {
            botsSender.sendLocation(responseContainer.getSendLocation());
            return;
        }
        if (responseContainer.hasAudio()) {
            botsSender.sendVoice(responseContainer.getSendVoice());
            return;
        }
        if (responseContainer.hasPicture()) {
            botsSender.sendPicture(responseContainer.getSendPhoto());
            return;
        }

        if (responseContainer.hasSticker()) {
            botsSender.sendSticker(responseContainer.getSendSticker());
            return;
        }
        if (responseContainer.hasPinnedMessage()) {
            PinnedMessageDTO pinnedMessageDTO = responseContainer.getPinnedMessageDTO();
            botsSender.sendPinnedMessage(pinnedMessageDTO);
        }
    }

    private void sendResponseToUserInPrivate(ResponseContainer responseContainer, TelegramWebhookCommandBot
            telegramWebhookBot) {
        var botsSender = (BotsSender) telegramWebhookBot;
        if (responseContainer.hasText()) {
            if (telegramWebhookBot.isCommand(responseContainer.getSendMessage().getText())) {
                var command = telegramWebhookBot.getRegisteredCommand(responseContainer.getSendMessage().getText());
                String[] arguments = new String[]{responseContainer.getMessage().getText()};
                var message = responseContainer.getMessage();
                command.processMessage(telegramWebhookBot, message, arguments);
            } else {
                botsSender.sendMessage(responseContainer.getSendMessage());
            }
            return;
        }
        sendSimpleMessages(responseContainer, botsSender);
    }

}
