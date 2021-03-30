package ru.kumkuat.application.GameModule.Commands.MarshakCommands;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class SupportCommand extends BotCommand {
    private static final String COMMAND_IDENTIFIER = "support";
    private static final String COMMAND_DESCRIPTION = "Если вам нужна помощь. Введите /support и получите столь необходимую поддержку.";

    public SupportCommand() {
        super(COMMAND_IDENTIFIER, COMMAND_DESCRIPTION);
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        SendMessage replyMessage = new SendMessage();
        replyMessage.setChatId(chat.getId().toString());
        replyMessage.enableHtml(true);
        Long userId = Long.valueOf(user.getId());


        replyMessage.setText("Раздел поддержки находится в разработке. Вам придется рассчитывать только на себя. Держитесь! Здоровя вам!");

        execute(absSender, replyMessage);
    }

    void execute(AbsSender sender, SendMessage message) {
        try {
            sender.execute(message);
        } catch (TelegramApiException e) {
        }
    }
}
