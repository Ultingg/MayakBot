package ru.kumkuat.application.gameModule.marshakCommands;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.kumkuat.application.gameModule.bot.MarshakBot;
import ru.kumkuat.application.gameModule.service.UserService;
@Service
public class OnlinePaymentService {
    private final MarshakBot marshakBot;
    private final UserService userService;

    public OnlinePaymentService(MarshakBot marshakBot, UserService userService) {
        this.marshakBot = marshakBot;
        this.userService = userService;
    }

    public void resolvePaymentProcessUpdate(Update update) {
        marshakBot.onWebhookUpdateReceived(update);
    }

    public void resolveCallbackQueryFromAdminListener(Update update) {
        var user = update.getCallbackQuery().getMessage().getFrom();
        user.setId(user.getId().equals(marshakBot.getId()) ? update.getCallbackQuery().getMessage().getChatId() : user.getId());
        userService.registerUser(update.getCallbackQuery().getMessage().getFrom());
        marshakBot.onWebhookUpdateReceived(update);
    }
}
