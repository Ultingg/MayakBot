package ru.kumkuat.application.GameModule.Abstract;

import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.CommandRegistry;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.ICommandRegistry;
import org.telegram.telegrambots.meta.api.methods.AnswerPreCheckoutQuery;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.payments.PreCheckoutQuery;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.*;
import java.util.function.BiConsumer;

public abstract class TelegramWebhookCommandBot extends TelegramWebhookBot implements ICommandRegistry {
    private final CommandRegistry commandRegistry;

    /**
     * Creates a TelegramWebhookCommandBot using default options
     * Use ICommandRegistry's methods on this bot to register commands
     */
    public TelegramWebhookCommandBot() {
        this(new DefaultBotOptions());
    }

    /**
     * Creates a TelegramWebhookCommandBot with custom options and allowing commands with
     * usernames
     * Use ICommandRegistry's methods on this bot to register commands
     *
     * @param options Bot options
     */
    public TelegramWebhookCommandBot(DefaultBotOptions options) {
        this(options, true);
    }

    /**
     * Creates a TelegramWebhookCommandBot
     * Use ICommandRegistry's methods on this bot to register commands
     *
     * @param options                   Bot options
     * @param allowCommandsWithUsername true to allow commands with parameters (default),
     *                                  false otherwise
     */
    public TelegramWebhookCommandBot(DefaultBotOptions options, boolean allowCommandsWithUsername) {
        super(options);
        this.commandRegistry = new CommandRegistry(allowCommandsWithUsername, this::getBotUsername);
    }

    public void SendAnswerPreCheckoutQuery(PreCheckoutQuery preCheckoutQuery) {
        AnswerPreCheckoutQuery answerPreCheckoutQuery = new AnswerPreCheckoutQuery();
        answerPreCheckoutQuery.setOk(true);
        answerPreCheckoutQuery.setPreCheckoutQueryId("1");

        try {
            System.out.println(this.execute(answerPreCheckoutQuery));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void DoAfterSuccessfulPayment(Update update) {
        SendMessage replyMessage = new SendMessage();
        replyMessage.setChatId(update.getMessage().getChat().getId().toString());
        replyMessage.enableHtml(true);
        replyMessage.setText("Payment complete!");
        try {
            this.execute(replyMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public final BotApiMethod onWebhookUpdateReceived(Update update) {
        if (update.hasPreCheckoutQuery()) {
            SendAnswerPreCheckoutQuery(update.getPreCheckoutQuery());
        } else if (update.hasMessage() && update.getMessage().hasSuccessfulPayment()) {
            DoAfterSuccessfulPayment(update);
        } else if (update.hasMessage()) {
            Message message = update.getMessage();
            if (message.hasDocument() && message.getCaption() != null) {
                message.setText(message.getCaption());
                message.setEntities(message.getCaptionEntities());
            }
            if (message.isCommand() && !filter(message)) {
                if (!commandRegistry.executeCommand(this, message)) {
                    //we have received a not registered command, handle it as invalid
                    return processInvalidCommandUpdate(update);
                }
                return null;
            }
        } else if (update.hasCallbackQuery()) {
            if (isCallbackQueryHasCommand(update.getCallbackQuery())) {
                try {
                    var command = getBotCommand(update.getCallbackQuery().getData());
                    command.processMessage(this, update.getCallbackQuery().getMessage(), new String[]{});
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        }
        return processNonCommandUpdate(update);
    }

    public boolean isCallbackQueryHasCommand(CallbackQuery callbackQuery) {
        return isCommand(callbackQuery.getData());
    }

    public boolean isCommand(String message) {
        for (var command : this.getRegisteredCommands()) {
            if (command.getCommandIdentifier().equals(message)) {
                return true;
            }
        }
        return false;
    }

    public IBotCommand getBotCommand(String commandIdentifier) throws Exception {
        for (var command : this.getRegisteredCommands()) {
            if (command.getCommandIdentifier().equals(commandIdentifier)) {
                return command;
            }
        }
        throw new Exception("Command is not found");
    }

    /**
     * This method is called when user sends a not registered command. By default it will just call processNonCommandUpdate(),
     * override it in your implementation if you want your bot to do other things, such as sending an error message
     *
     * @param update Received update from Telegram
     */
    protected BotApiMethod processInvalidCommandUpdate(Update update) {
        return processNonCommandUpdate(update);
    }

    /**
     * Override this function in your bot implementation to filter messages with commands
     * <p>
     * For example, if you want to prevent commands execution incoming from group chat:
     * #
     * # return !message.getChat().isGroupChat();
     * #
     *
     * @param message Received message
     * @return true if the message must be ignored by the command bot and treated as a non command message,
     * false otherwise
     * @note Default implementation doesn't filter anything
     */
    protected boolean filter(Message message) {
        return false;
    }

    @Override
    public final boolean register(IBotCommand botCommand) {
        return commandRegistry.register(botCommand);
    }

    @Override
    public final Map<IBotCommand, Boolean> registerAll(IBotCommand... botCommands) {
        return commandRegistry.registerAll(botCommands);
    }

    @Override
    public final boolean deregister(IBotCommand botCommand) {
        return commandRegistry.deregister(botCommand);
    }

    @Override
    public final Map<IBotCommand, Boolean> deregisterAll(IBotCommand... botCommands) {
        return commandRegistry.deregisterAll(botCommands);
    }

    @Override
    public final Collection<IBotCommand> getRegisteredCommands() {
        return commandRegistry.getRegisteredCommands();
    }

    @Override
    public void registerDefaultAction(BiConsumer<AbsSender, Message> defaultConsumer) {
        commandRegistry.registerDefaultAction(defaultConsumer);
    }

    @Override
    public final IBotCommand getRegisteredCommand(String commandIdentifier) {
        return commandRegistry.getRegisteredCommand(commandIdentifier);
    }

    /**
     * @return Bot username
     */
    @Override
    public abstract String getBotUsername();

    /**
     * Process all updates, that are not commands.
     *
     * @param update the update
     * @warning Commands that have valid syntax but are not registered on this bot,
     * won't be forwarded to this method <b>if a default action is present</b>.
     */
    public abstract BotApiMethod processNonCommandUpdate(Update update);
}
