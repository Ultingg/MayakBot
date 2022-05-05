package ru.kumkuat.application.GameModule.Controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
@RestController
@AllArgsConstructor
public class UpdateController {
    private final BotController botController;

    @PostMapping(value = "/listener")
    public void receivedUpdateFromSimpleListener(@RequestBody Update update) {

        if(update.hasMessage()) {
            boolean bot =  update.getMessage().hasViaBot();
            log.info("update sended by bot: {}", bot);
            log.info("Incoming update to path '/listener' from chat with id: {}  from user {}", update.getMessage().getChatId(), update.getMessage().getFrom().getId());
           try {
               botController.resolveUpdatesFromSimpleListener(update.getMessage());
           }catch (HttpMessageNotReadableException e){
               log.info("CATCHED SOME STRANSGE EXCEPTION");
          // }
        }
        }
    }

    @PostMapping(value = "/marshak")
    public void receivedUpdateFromAdminListener(@RequestBody Update update) {
        if (update.hasCallbackQuery()) {
            log.info("CallbackQuery from chat with id: {}", Long.valueOf(update.getCallbackQuery().getFrom().getId()));
            botController.resolveCallbackQueryFromAdminListener(update);
        } else if (update.hasPreCheckoutQuery()) {
            log.info("PerCheckoutQuery from chat with id: {}", Long.valueOf(update.getPreCheckoutQuery().getFrom().getId()));
            botController.resolvePerCheckoutQuery(update);
        }
        if (update.hasMessage()) {
            log.info("Incoming update to path '/admin' from chat with id: {}  from user {}", update.getMessage().getChatId(), update.getMessage().getFrom().getId());
            if (update.getMessage().isCommand()) {
                log.info("Command from chat with id: {}", update.getMessage().getChatId());
                botController.resolveCommandMessage(update);
            } else {
                botController.resolveUpdatesFromAdminListener(update.getMessage());
            }
        }
    }
}
