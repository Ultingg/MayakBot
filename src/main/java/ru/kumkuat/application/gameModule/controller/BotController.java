package ru.kumkuat.application.gameModule.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.kumkuat.application.gameModule.Abstract.TelegramWebhookCommandBot;
import ru.kumkuat.application.gameModule.bot.*;
import ru.kumkuat.application.gameModule.models.User;
import ru.kumkuat.application.gameModule.promocode.Service.PromocodeLogeService;
import ru.kumkuat.application.gameModule.promocode.Service.PromocodeService;
import ru.kumkuat.application.gameModule.service.ResponseOperatorService;
import ru.kumkuat.application.gameModule.service.TelegramChatService;
import ru.kumkuat.application.gameModule.service.UserService;

import java.util.List;

@Component
public class BotController {

    Logger log = LoggerFactory.getLogger(BotController.class.getName());

    @Autowired
    private List<BotsSender> botCollection;
    private final UserService userService;
    private final TelegramChatService telegramChatService;
    private final PromocodeLogeService promocodeLogeService;
    private final PromocodeService promocodeService;

    private final ResponseOperatorService responseOperatorService;

    public BotController(MarshakBot marshakBot,
                         Brodskiy brodskiy, UserService userService, TelegramChatService telegramChatService,
                         PromocodeLogeService promocodeLogeService, PromocodeService promocodeService, ResponseOperatorService responseOperatorService) {
        this.userService = userService;
        this.telegramChatService = telegramChatService;
        this.promocodeLogeService = promocodeLogeService;
        this.promocodeService = promocodeService;
        this.responseOperatorService = responseOperatorService;

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
        if (isUserAPlayer(updateMessage, user)) {
            responseOperatorService.responseTypeResolver(updateMessage);
        }
    }

    private boolean isUserAPlayer(Message updateMessage, User user) {
        return user != null && !user.isAdmin()
                && !updateMessage.getChat().getType().equals("private")
                && !commandChecker(updateMessage);
    }

    public void resolveUpdatesFromAdminListener(Message updateMessage) {
        userService.registerUser(updateMessage.getFrom());
        if (updateMessage.hasText()
                && (updateMessage.getChat().getType().equals("private"))) {
            promoResolve(updateMessage);
            User user = userService.getUserByTelegramId(updateMessage.getFrom().getId());
            if ((user.isPlaying() && !telegramChatService.isUserAlreadyGetChat(user.getTelegramUserId()))) {
                responseOperatorService.responseTypeResolver(updateMessage);
            }
        }
    }

    private boolean commandChecker(Message message) {
        return botCollection.stream().filter(bot -> bot instanceof TelegramWebhookCommandBot)
                .anyMatch(bot -> ((TelegramWebhookCommandBot) bot).isCommand(message.getText()));
    }


    private void promoResolve(Message updateMessage) {
        User user = userService.getUserByTelegramId(updateMessage.getFrom().getId());
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
}

