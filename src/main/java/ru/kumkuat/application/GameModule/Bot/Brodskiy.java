package ru.kumkuat.application.GameModule.Bot;

import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

public class Brodskiy extends BotCommand {
    public Brodskiy(String commandIdentifier, String description) {
        super(commandIdentifier, description);

    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {

    }
}
