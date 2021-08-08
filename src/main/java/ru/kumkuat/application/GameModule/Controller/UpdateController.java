package ru.kumkuat.application.GameModule.Controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.kumkuat.application.GameModule.Bot.Brodskiy;
import ru.kumkuat.application.GameModule.Bot.MarshakBot;
import ru.kumkuat.application.GameModule.Models.User;
import ru.kumkuat.application.GameModule.Service.ResponseService;
import ru.kumkuat.application.GameModule.Service.TelegramChatService;
import ru.kumkuat.application.GameModule.Service.UserService;

@Slf4j
@RestController
@AllArgsConstructor
public class UpdateController {

    private final TelegramChatService telegramChatService;
    private final MarshakBot marshakBot;
    private final Brodskiy brodskiy; // бот слушатель
    private final ResponseService responseService;
    private final UserService userService;
    private final BotController botController;

//    @PostMapping(value = "/")
//    public void receivedUpdateFromSimpleListener(@RequestBody Update update) {
//        if (update.hasMessage() && !update.getMessage().getChat().getType().equals("private")) {     //проверка что Листнер видит update только в Беседке
//            botController.resolveUpdatesFromSimpleLIstner(update.getMessage());
//        }else {
//            marshakBot.onWebhookUpdateReceived(update);
//        }
//    }
//    @PostMapping(value = "/admin")
//    public void receivedUpdateFromAdminListener(@RequestBody Update update) {
//        if (update.hasMessage() &&
//               update.getMessage().getFrom().getId().equals(update.getMessage().getChatId())) {       // проверка что Админ видит update только в личке
//
//                  botController.resolveUpdatesFromAdminLIstner(update.getMessage());
//              } else {
//            marshakBot.onWebhookUpdateReceived(update);
//        }
//    }



    @RequestMapping(value = "/", method = RequestMethod.POST)
    public void onUpdateReceived(@RequestBody Update update) {
        Message message = update.getMessage();
        if (message != null && !commandChecker(message) &&                      /**Check if the sended message wasn't a command **/
                userService.IsUserExist(message.getFrom().getId().longValue()) &&           /**Check if the message sent from existing user **/
                !userService.getUserByTelegramId(message.getFrom().getId().longValue()).isAdmin() &&  /**Check if the message sent from Admin **/
                !message.getChat().getType().equals("private")) {                       /**Check if the message sent to private chat to bot **/
            Thread myThready = new Thread(new CallBotResponse(update));
            myThready.start();
        } else if (message != null && commandChecker(message)) {
            marshakBot.onWebhookUpdateReceived(update);
        }
    }

    @RequestMapping(value = "/admin", method = RequestMethod.POST)
    public void onUpdateReceiver(@RequestBody Update update) {
        log.debug("Received by Marshak command.");
        User user = null;

        if (update.hasMessage() && userService.IsUserExist(update.getMessage().getFrom().getId().longValue())) {
            user = userService.getUserByTelegramId(update.getMessage().getFrom().getId().longValue());
        } else {
            marshakBot.onWebhookUpdateReceived(update);
        }

        if (user != null && user.getTelegramUserId().equals(update.getMessage().getChatId())) { //проверка приватности чата игнор беседки чтобы не дублировать


            if (user.isPlaying() &&
                    !telegramChatService.isUserAlreadyPlaying(user.getTelegramUserId()) &&
                    !commandChecker(update.getMessage())) {
                new Thread(new CallBotResponse(update)).start();
            } else {
                marshakBot.onWebhookUpdateReceived(update);
            }
        } else if (user != null && user.isAdmin()) {
            marshakBot.onWebhookUpdateReceived(update);
        }
    }

    private boolean commandChecker(Message message) {
        boolean result = false;
        if (message.hasText()) {
            String textToCheck = message.getText();
            if (textToCheck.contains("/")) {
                result = true;
            }
        }
        return result;
    }

    private boolean navigationCommandCheck(Message message) {
        boolean result = false;
        if (message.hasText()) {
            String textToCheck = message.getText();
            if (textToCheck.contains("/next") || textToCheck.contains("/previous")) {
                result = true;
            }
        }
        return result;
    }

    class CallBotResponse implements Runnable {
        Update update;

        CallBotResponse(Update update) {
            this.update = update;
        }

        @Override
        public void run() {
            System.out.println("Привет из побочного потока!");
            Message incomingMessage = update.getMessage();
            if (incomingMessage != null && commandChecker(incomingMessage)) {
                log.debug("Received throw to Marshak.");
                responseService.messageReceiver(incomingMessage, navigationCommandCheck(incomingMessage));
            } else {
                log.debug("Received message.");
                responseService.messageReceiver(incomingMessage, false);
            }

        }
    }
}
