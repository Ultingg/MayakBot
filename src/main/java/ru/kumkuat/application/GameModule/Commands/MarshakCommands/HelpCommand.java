package ru.kumkuat.application.GameModule.Commands.MarshakCommands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.ICommandRegistry;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.helpCommand.IManCommand;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.helpCommand.ManCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.kumkuat.application.GameModule.Service.UserService;

import java.util.ArrayList;
import java.util.Collection;

@Component
public class HelpCommand extends ManCommand {
    private static final String COMMAND_IDENTIFIER = "help";
    private static final String COMMAND_DESCRIPTION = "Вывести список доступных команд";
    private static final String EXTENDED_DESCRIPTION = "This command displays all commands the bot has to offer.\n /help can display deeper information";
    @Autowired
    private UserService userService;

    /**
     * Create a Help command with the standard Arguments.
     */
    public HelpCommand() {
        super(COMMAND_IDENTIFIER, COMMAND_DESCRIPTION, EXTENDED_DESCRIPTION);
        ignorCommandList.add("start");
        ignorCommandList.add("support");
        ignorCommandList.add("sentchat");
        ignorCommandList.add("play");
    }

    /**
     * Creates a Help Command with custom identifier, description and extended Description
     *
     * @param commandIdentifier   the unique identifier of this command (e.g. the command string to enter into chat)
     * @param description         the description of this command
     * @param extendedDescription The extended Description for the Command, should provide detailed information about arguments and possible options
     */
    public HelpCommand(String commandIdentifier, String description, String extendedDescription) {
        super(commandIdentifier, description, extendedDescription);
    }

    /**
     * Returns the command and description of all supplied commands as a formatted String
     *
     * @param botCommands the Commands that should be included in the String
     * @return a formatted String containing command and description for all supplied commands
     */
    public static String getHelpText(boolean adminFlag, IBotCommand... botCommands) {
        StringBuilder reply = new StringBuilder();

        for (IBotCommand com : botCommands) {
            if (!adminFlag) {
                if (!(com instanceof AdminCommand) && !ignorCommandList.contains(com.getCommandIdentifier())) {
                    reply.append(com.toString()).append(System.lineSeparator()).append(System.lineSeparator());
                }
            } else {
                reply.append(com.toString()).append(System.lineSeparator()).append(System.lineSeparator());
            }

        }
        return reply.toString();
    }

    private static ArrayList<String> ignorCommandList = new ArrayList<String>();

    /**
     * Returns the command and description of all supplied commands as a formatted String
     *
     * @param botCommands a collection of commands that should be included in the String
     * @return a formatted String containing command and description for all supplied commands
     */
    public static String getHelpText(Collection<IBotCommand> botCommands, boolean adminFlag) {
        return getHelpText(adminFlag, botCommands.toArray(new IBotCommand[botCommands.size()]));
    }

    /**
     * Returns the command and description of all supplied commands as a formatted String
     *
     * @param registry a commandRegistry which commands are formatted into the String
     * @return a formatted String containing command and description for all supplied commands
     */
    public static String getHelpText(ICommandRegistry registry, boolean adminFlag) {
        return getHelpText(registry.getRegisteredCommands(), adminFlag);
    }

    /**
     * Reads the extended Description from a BotCommand. If the Command is not of Type {@link IManCommand}, it calls toString();
     *
     * @param command a command the extended Descriptions is read from
     * @return the extended Description or the toString() if IManCommand is not implemented
     */
    public static String getManText(IBotCommand command) {
        return IManCommand.class.isInstance(command) ? getManText((IManCommand) command) : command.toString();
    }

    /**
     * Reads the extended Description from a BotCommand;
     *
     * @param command a command the extended Descriptions is read from
     * @return the extended Description
     */
    public static String getManText(IManCommand command) {
        return command.toMan();
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        String reply;
        ru.kumkuat.application.GameModule.Models.User userFromDB = userService.getUser(Long.valueOf(user.getId()));
        boolean adminFlag = userFromDB.isAdmin();
        if (ICommandRegistry.class.isInstance(absSender)) {
            ICommandRegistry registry = (ICommandRegistry) absSender;
//            if (arguments.length > 0) {
//                IBotCommand command = registry.getRegisteredCommand(arguments[0]);
//                reply = getManText(command);
//            } else {
//                reply = getHelpText(registry, adminFlag);
//            }
            reply = getHelpText(registry, adminFlag);
            try {
                absSender.execute(SendMessage.builder().chatId(chat.getId().toString()).text(reply).parseMode("HTML").build());
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }
}
