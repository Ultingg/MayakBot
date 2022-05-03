package ru.kumkuat.application.GameModule.Controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
@RestController
@AllArgsConstructor
public class UpdateController {
    private final BotController botController;

    @PostMapping(value = "/")
    public void receivedUpdateFromSimpleListener(@RequestBody Update update) {
        if(update.hasMessage()){
            log.info("Incoming update to path '/' from chat with id: {}", update.getMessage().getChatId());
        }

        botController.resolveUpdatesFromSimpleListener(update.getMessage());
    }

    @PostMapping(value = "/admin")
    public void receivedUpdateFromAdminListener(@RequestBody Update update) {
        if(update.hasMessage()){
            log.info("Incoming update to path '/admin' from chat with id: {}", update.getMessage().getChatId());
        }
        if (update.hasCallbackQuery()) {
                log.info("CallbackQuery from chat with id: {}", update.getCallbackQuery().getFrom().getId());
            botController.resolveCallbackQueryFromAdminListener(update);
        } else if(update.hasPreCheckoutQuery()) {
            log.info("PerCheckoutQuery from chat with id: {}", update.getPreCheckoutQuery().getFrom().getId());
            botController.resolvePerCheckoutQuery(update);
        }
        else if(update.getMessage().isCommand()) {
            log.info("Command from chat with id: {}", update.getMessage().getChatId());
            botController.resolveCommandMessage(update);
        }
        else {

            botController.resolveUpdatesFromAdminListener(update.getMessage());
        }
    }
}
