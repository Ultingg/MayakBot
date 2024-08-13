package ru.kumkuat.application.gameModule.marshakCommands;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.kumkuat.application.gameModule.bot.AkhmatovaBot;
import ru.kumkuat.application.gameModule.bot.Brodskiy;
import ru.kumkuat.application.gameModule.bot.Harms;
import ru.kumkuat.application.gameModule.bot.MayakBot;
import ru.kumkuat.application.gameModule.service.BGUserService;
import ru.kumkuat.application.gameModule.service.UserService;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@PropertySource(value = "file:../resources/messages.yml", encoding = "UTF-8")
public class StartCommand extends BotCommand {

    private final UserService userService;
    private final BGUserService bgUserService;

    @Autowired
    private Harms harms;
    @Autowired
    private AkhmatovaBot akhmatovaBot;
    @Autowired
    private Brodskiy brodskiy;
    @Autowired
    private MayakBot mayakBot;

    @Value("${message.greeting.one}")
    private String greetingOne;
    @Value("${message.greeting.two}")
    private  String greetingTwo;
    @Value("${message.greeting.three}")
    private String greetingThree;
    @Value("${message.greeting.four}")
    private String greetingFour;
    @Value("${message.greeting.five}")
    private String greetingFive;
    @Value("${message.greeting.pay_start}")
    private String payStartMessage;
    @Value("${message.greeting.pay_button}")
    private String payButtonName;
    @Value("${message.greeting.start_button}")
    private String startWalkButtonName;


    public StartCommand(UserService userService, BGUserService bgUserService) {
        super("/start", "to start!\n");
        this.userService = userService;
        this.bgUserService = bgUserService;
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        if (user.getId().longValue() == chat.getId()) {
            log.info("Marshak start command start executing");
            registerUser(user);

            sendMessage(absSender, user, chat.getId().toString(), greetingOne);
            sendMessage(absSender, user, chat.getId().toString(), greetingTwo);
            sendMessage(absSender, user, chat.getId().toString(), greetingThree);
            sendMessage(absSender, user, chat.getId().toString(), greetingFour);
            sendMessage(absSender, user, chat.getId().toString(), greetingFive);

            List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
            rowList.add(getRowOfInlineButtonWithCallback(payButtonName, "pay"));
            rowList.add(getRowOfInlineButtonWithCallback(startWalkButtonName,"play"));
            InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
            inlineKeyboardMarkup.setKeyboard(rowList);

            sendMessageWithKeyBoard(absSender, user, chat.getId().toString(), inlineKeyboardMarkup, payStartMessage);
            log.info("Marshak start command finished executing");
        }
    }
    /**
     * Registration of users.
     *
     * @param user to register in DB
     */
    private void registerUser(User user) {
        if (!userService.IsUserExist(user.getId().longValue())) {
            try {
                userService.setUserIntoDB(user);
                log.info("User id: {} registered", user.getId());
            } catch (Exception e) {
                log.info("registration failed", e);
            }
        }
    }

    void execute(AbsSender sender, SendMessage message, User user) {
        try {
            sender.execute(message);
        } catch (TelegramApiException e) {
            log.debug("Sending of message by Marshak was failed! IDIOT!");
        }
    }

    private void sendMessage(AbsSender absSender, User user, String chatId, String text) {
        SendMessage replyMessage = new SendMessage();
        replyMessage.setChatId(chatId);
        replyMessage.enableHtml(true);
        replyMessage.setText(text);
        execute(absSender, replyMessage, user);
    }

    private void sendMessageWithKeyBoard(AbsSender absSender, User user, String chatId, InlineKeyboardMarkup markup, String text) {
        SendMessage replyMessage = new SendMessage();
        replyMessage.setChatId(chatId);
        replyMessage.enableHtml(true);
        replyMessage.setText(text);
        replyMessage.setReplyMarkup(markup);
        execute(absSender, replyMessage, user);
    }

    private List<InlineKeyboardButton> getRowOfInlineButtonWithCallback(String text, String callback) {
        List<InlineKeyboardButton> keyboardButtonsRow = new ArrayList<>();
        var Button21 = new InlineKeyboardButton();
        Button21.setText(text);
        Button21.setCallbackData(callback);
        keyboardButtonsRow.add(Button21);
        return keyboardButtonsRow;
    }
}
