package ru.kumkuat.application.gameModule.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.kumkuat.application.gameModule.marshakCommands.CommandService;
import ru.kumkuat.application.gameModule.marshakCommands.OnlinePaymentService;

@Slf4j
@RestController
public class UpdateController {
    private final BotController botController;
    private final CommandService commandService;
    private final OnlinePaymentService onlinePaymentService;

    public UpdateController(BotController botController, CommandService commandService, OnlinePaymentService onlinePaymentService) {
        this.botController = botController;
        this.commandService = commandService;
        this.onlinePaymentService = onlinePaymentService;
    }


    @PostMapping(value = "/listener")
    public void receivedUpdateFromSimpleListener(@RequestBody Update update) {

        if (update.hasMessage()) {
            boolean bot = update.getMessage().hasViaBot();
            log.info("update sended by bot: {}", bot);
            log.info("Incoming update to path '/listener' from chat with id: {}  from user {}", update.getMessage().getChatId(), update.getMessage().getFrom().getId());
            try {
                botController.resolveUpdatesFromSimpleListener(update.getMessage());
            } catch (HttpMessageNotReadableException e) {
                log.info("CAUGHT SOME STRANGE EXCEPTION");

            }
        }
    }

    @PostMapping(value = "/marshak")
    public void receivedUpdateFromAdminListener(@RequestBody Update update) {
        if (update.hasCallbackQuery()) {
            log.info("CallbackQuery from chat with id: {}", update.getCallbackQuery().getFrom().getId());
            onlinePaymentService.resolveCallbackQueryFromAdminListener(update);
        } else if (update.hasPreCheckoutQuery()) {
            log.info("PerCheckoutQuery from chat with id: {}", update.getPreCheckoutQuery().getFrom().getId());
            onlinePaymentService.resolvePaymentProcessUpdate(update);
        }
        if (update.hasMessage()) {
            log.info("Incoming update to path '/admin' from chat with id: {}  from user {}", update.getMessage().getChatId(), update.getMessage().getFrom().getId());
            if (update.getMessage().isCommand()) {
                log.info("Command from chat with id: {}", update.getMessage().getChatId());
                commandService.resolveCommandMessage(update);
            } else if (update.getMessage().hasSuccessfulPayment()) {
                onlinePaymentService.resolvePaymentProcessUpdate(update);
            } else {
                botController.resolveUpdatesFromAdminListener(update.getMessage());
            }
        }
    }
}

