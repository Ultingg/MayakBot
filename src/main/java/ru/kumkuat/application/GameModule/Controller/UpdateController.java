package ru.kumkuat.application.GameModule.Controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.kumkuat.application.GameModule.Bot.MarshakBot;
import ru.kumkuat.application.GameModule.Service.ResponseService;

import java.util.Objects;

@Slf4j
@RestController
@AllArgsConstructor
public class UpdateController {
    private final MarshakBot marshakBot;
    private final ResponseService responseService;
    private final BotController botController;

    @PostMapping(value = "/")
    public void receivedUpdateFromSimpleListener(@RequestBody Update update) {
        if (update.hasMessage()
                && isNotCommand(update.getMessage())
                && !update.getMessage().getChat().getType().equals("private")) {     //проверка что Листнер видит update только в Беседке
            botController.resolveUpdatesFromSimpleLIstner(update.getMessage());
        }
    }

    @PostMapping(value = "/admin")
    public void receivedUpdateFromAdminListener(@RequestBody Update update) {
        if (update.hasMessage()
                && Objects.equals(Long.valueOf(update.getMessage().getFrom().getId()), update.getMessage().getChatId())// проверка что Админ видит update только в личке
                && isNotCommand(update.getMessage())) {
            botController.resolveUpdatesFromAdminLIstner(update.getMessage());
        } else {
            marshakBot.onWebhookUpdateReceived(update);
        }
    }

    private boolean isNotCommand(Message message) {
        return !(message.hasText() && (message.getText().contains("/")));
    }
}
