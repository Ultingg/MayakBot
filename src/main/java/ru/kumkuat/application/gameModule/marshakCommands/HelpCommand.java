package ru.kumkuat.application.gameModule.marshakCommands;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.helpCommand.ManCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.kumkuat.application.gameModule.exceptions.TelegramCommandException;
import ru.kumkuat.application.gameModule.models.TelegramChat;
import ru.kumkuat.application.gameModule.service.TelegramChatService;
import ru.kumkuat.application.gameModule.service.UserService;
@Slf4j
@Component
public class HelpCommand extends ManCommand {
    private static final String COMMAND_IDENTIFIER = "help";
    private static final String COMMAND_DESCRIPTION = "Вывести список доступных команд";
    private static final String EXTENDED_DESCRIPTION = "This command displays all commands the bot has to offer.\n /help can display deeper information";

    @Autowired
    private UserService userService;

    @Autowired
    private TelegramChatService telegramChatService;

    public HelpCommand() {
        super(COMMAND_IDENTIFIER, COMMAND_DESCRIPTION, EXTENDED_DESCRIPTION);
    }

    public HelpCommand(String commandIdentifier, String description, String extendedDescription) {
        super(commandIdentifier, description, extendedDescription);
    }

    private void execute(AbsSender absSender,SendMessage sendMessage) throws TelegramCommandException {
        try {
            absSender.execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new TelegramCommandException();
        }
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        try {

            Long userId = Long.valueOf(user.getId());
            if (userService.getUserByTelegramId(userId).isAdmin() && arguments != null && arguments.length > 0) {
                var qweryuserId = Long.valueOf(arguments[0]);
                sendUserInfoMessage(absSender, qweryuserId);
            }
            else{
                sendUserInfoMessage(absSender, userId);

                SendMessage sendMessage = new SendMessage();
                sendMessage.enableHtml(true);
                sendMessage.setChatId(chat.getId().toString());
                sendMessage.setText("Ваш запрос успешно направлен в поддержку");
                execute(absSender, sendMessage);
                log.info("User with id: " + userId + "get notified about requset for help");
            }
        } catch (TelegramCommandException e) {
            e.printStackTrace();
            e.getLogMessage(this, "when sending msg to user.");
        }
    }

    public void sendUserInfoMessage(AbsSender absSender, Long userTelegeramId){
        try {
            if (userService.IsUserExist(userTelegeramId)) {
                var userdb = userService.getUserByTelegramId(userTelegeramId);

                String reply = "Пользователь:\n";
                reply += "юзер телеграм id: " + userdb.getTelegramUserId() + "\n";
                reply += "юзер id: " + userdb.getId() + "\n";
                reply += "юзер sceneid: " + userdb.getSceneId() + "\n";
                reply += "юзер isPlaying: " + userdb.isPlaying() + "\n";
                reply += "юзер isTriggered: " + userdb.isTriggered() + "\n";
                reply += "юзер hasPaying: " + userdb.isHasPay() + "\n";
                reply += "username: " + userdb.getName() + "\n";

                if(telegramChatService.isUserAlreadyGetChat(userTelegeramId)){
                    TelegramChat chatdb = telegramChatService.getChatByUserTelegramId(userTelegeramId);
                    reply += "чат телеграм id: " + chatdb.getChatId() + "\n";
                    reply += "чат id: " + chatdb.getId() + "\n";
                }
                log.info("User with id: " + userdb.getId() + "asked for HELP");
                SendMessage sendMessage = new SendMessage();
                sendMessage.enableHtml(true);
                sendMessage.setChatId(telegramChatService.getAdminChatId());
                sendMessage.setText(reply);
                execute(absSender,sendMessage);
            }
        } catch (TelegramCommandException e) {
            e.printStackTrace();
            e.getLogMessage(this, "when sending msg to Admin" );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
