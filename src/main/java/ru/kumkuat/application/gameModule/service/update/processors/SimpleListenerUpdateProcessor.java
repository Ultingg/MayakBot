package ru.kumkuat.application.gameModule.service.update.processors;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.kumkuat.application.gameModule.Abstract.TelegramWebhookCommandBot;
import ru.kumkuat.application.gameModule.bot.BotsSender;
import ru.kumkuat.application.gameModule.collections.ResponseContainer;
import ru.kumkuat.application.gameModule.models.User;
import ru.kumkuat.application.gameModule.service.ResponseService;
import ru.kumkuat.application.gameModule.service.UserService;

import java.util.List;
@Slf4j
@Service
public class SimpleListenerUpdateProcessor implements UpdateProcessor {
    private final List<BotsSender> botCollection;
    private final ResponseService responseService;
    private final UserService userService;

    public SimpleListenerUpdateProcessor(List<BotsSender> botCollection, ResponseService responseService, UserService userService) {
        this.botCollection = botCollection;
        this.responseService = responseService;
        this.userService = userService;
    }


    @Override
    public void processUpdate(Message updateMessage) {
        long userId =  userService.getCheckedUserId(updateMessage);
        log.info("user id from update: {}", userId);
        User user = userService.getUserByTelegramId(userId);
        if (user != null && !user.isAdmin()
                && !updateMessage.getChat().getType().equals("private")
                && !commandChecker(updateMessage)) {
            run(updateMessage);
        }
    }


    private boolean commandChecker(Message message) {
        return botCollection.stream().filter(bot -> bot instanceof TelegramWebhookCommandBot)
                .anyMatch(bot -> ((TelegramWebhookCommandBot) bot).isCommand(message.getText()));
    }


    private void  run(Message incomingMessage) {
        log.info("Another thread launched!");
        List<ResponseContainer> responseContainers = responseService.messageReceiver(incomingMessage);
        if (incomingMessage.hasText() && commandChecker(incomingMessage)) {
            /*зачем тут проверка если далее обработка одинаковая*/
            log.info("Received throw to Marshak.");

//            responseResolver(responseService.messageReceiver(incomingMessage));
        } else {
            log.info("Received message.");
//            responseResolver(responseService.messageReceiver(incomingMessage));
        }
    }
}
