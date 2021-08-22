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
        botController.resolveUpdatesFromSimpleListener(update.getMessage());
    }

    @PostMapping(value = "/admin")
    public void receivedUpdateFromAdminListener(@RequestBody Update update) {
        if (update.hasCallbackQuery()) {
            botController.resolveCallbackQueryFromAdminListener(update);
        } else {
            botController.resolveUpdatesFromAdminListener(update.getMessage());
        }
    }
}
