package ru.kumkuat.application.GameModule.Commands.MarshakCommands;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.kumkuat.application.GameModule.Bot.AkhmatovaBot;
import ru.kumkuat.application.GameModule.Bot.Brodskiy;
import ru.kumkuat.application.GameModule.Bot.Harms;
import ru.kumkuat.application.GameModule.Bot.MayakBot;
import ru.kumkuat.application.GameModule.Service.BGUserService;
import ru.kumkuat.application.GameModule.Service.TelegramChatService;
import ru.kumkuat.application.GameModule.Service.UserService;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
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
    @Autowired
    private TelegramChatService telegramChatService;

    @Autowired
    private HelpCommand helpCommand;

    public StartCommand(UserService userService, BGUserService bgUserService) {
        super("/start", "to start!\n");
        this.userService = userService;
        this.bgUserService = bgUserService;
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        if (user.getId().longValue() == chat.getId()) {

            log.debug("Marshak ");

            registerUser(user);


            InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
            var ListButtonCollections = new ArrayList<List<InlineKeyboardButton>>();

            var InlineKeyboardButtonCollection = new ArrayList<InlineKeyboardButton>();
            InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
            inlineKeyboardButton.setText("Бродский");
//            inlineKeyboardButton.setUrl("https://t.me/IABrodskiyTestBot?start");
            inlineKeyboardButton.setUrl("https://t.me/".concat(brodskiy.getBotUsername().concat("?start")));
            InlineKeyboardButtonCollection.add(inlineKeyboardButton);
            ListButtonCollections.add(InlineKeyboardButtonCollection);

            InlineKeyboardButtonCollection = new ArrayList<InlineKeyboardButton>();
            inlineKeyboardButton = new InlineKeyboardButton();
            inlineKeyboardButton.setText("Маяковский");
            inlineKeyboardButton.setUrl("https://t.me/".concat(mayakBot.getBotUsername().concat("?start")));
            InlineKeyboardButtonCollection.add(inlineKeyboardButton);
            ListButtonCollections.add(InlineKeyboardButtonCollection);

            InlineKeyboardButtonCollection = new ArrayList<InlineKeyboardButton>();
            inlineKeyboardButton = new InlineKeyboardButton();
            inlineKeyboardButton.setText("Хармс");
            inlineKeyboardButton.setUrl("https://t.me/".concat(harms.getBotUsername().concat("?start")));
            InlineKeyboardButtonCollection.add(inlineKeyboardButton);
            ListButtonCollections.add(InlineKeyboardButtonCollection);

            InlineKeyboardButtonCollection = new ArrayList<InlineKeyboardButton>();
            inlineKeyboardButton = new InlineKeyboardButton();
            inlineKeyboardButton.setText("Ахматова");
            inlineKeyboardButton.setUrl("https://t.me/".concat(akhmatovaBot.getBotUsername().concat("?start")));
            InlineKeyboardButtonCollection.add(inlineKeyboardButton);
            ListButtonCollections.add(InlineKeyboardButtonCollection);

            markup.setKeyboard(ListButtonCollections);

            SendMessage replyMessage = new SendMessage();
            replyMessage.setChatId(chat.getId().toString());
            replyMessage.enableHtml(true);
            replyMessage.setText("Привет! Мы рады приветствовать тебя!");
            execute(absSender, replyMessage, user);


            replyMessage = new SendMessage();
            replyMessage.setChatId(chat.getId().toString());
            replyMessage.enableHtml(true);
            replyMessage.setText("Чтобы начать тебе нужно активировать авторов!");
            execute(absSender, replyMessage, user);

            replyMessage = new SendMessage();
            replyMessage.setChatId(chat.getId().toString());
            replyMessage.enableHtml(true);
            replyMessage.setText("Перейди по каждой ссылке и нажми старт");
            replyMessage.setReplyMarkup(markup);
            execute(absSender, replyMessage, user);


            List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
            var Button11 = new InlineKeyboardButton();
            Button11.setText("Оплата");
            Button11.setCallbackData("pay");
            keyboardButtonsRow1.add(Button11);
            List<InlineKeyboardButton> keyboardButtonsRow2 = new ArrayList<>();
            var Button21 = new InlineKeyboardButton();
            Button21.setText("Начать прогулку");
            Button21.setCallbackData("play");
            keyboardButtonsRow2.add(Button21);
            List<InlineKeyboardButton> keyboardButtonsRow3 = new ArrayList<>();
            //var Button31 = new InlineKeyboardButton();
            //Button31.setText("Иду на спектакль 31 июля");
            //Button31.setCallbackData("play_marathon");
            //keyboardButtonsRow3.add(Button31);

            List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
            rowList.add(keyboardButtonsRow1);
            rowList.add(keyboardButtonsRow2);
            rowList.add(keyboardButtonsRow3);

            InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
            inlineKeyboardMarkup.setKeyboard(rowList);

            replyMessage = new SendMessage();
            replyMessage.setChatId(chat.getId().toString());
            replyMessage.enableHtml(true);
            replyMessage.setText("Нажми, когда будешь готов!");
            replyMessage.setReplyMarkup(inlineKeyboardMarkup);
            execute(absSender, replyMessage, user);

            replyMessage = new SendMessage();
            replyMessage.setChatId(chat.getId().toString());
            replyMessage.enableHtml(true);
            replyMessage.setText("Если у тебя возникли какие-то трудности, воспользуйстя командой /help. \n" +
                    "Мы постараемся помочь.");
            execute(absSender, replyMessage, user);

            replyMessage = new SendMessage();
            replyMessage.setChatId(chat.getId().toString());
            replyMessage.enableHtml(true);
            replyMessage.setText("Или ты всегда можешь написать нам на почту. \n" +
                    "teatr.prospektspb@gmail.com");
            execute(absSender, replyMessage, user);

            replyMessage = new SendMessage();
            replyMessage.setChatId(chat.getId().toString());
            replyMessage.enableHtml(true);
            replyMessage.setText("Всю информацию о нас можно найти в группе: \n" +
                    "https://vk.com/prospektspb");
            execute(absSender, replyMessage, user);

//            helpCommand.execute(absSender, user, chat, arguments);
        }
    }

    /**
     * Registration of users.
     * @param user
     */
    private void registerUser(User user) {
        if (!userService.IsUserExist(user.getId().longValue())) {
            try {
                userService.setUserIntoDB(user);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    void execute(AbsSender sender, SendMessage message, User user) {
        try {
            sender.execute(message);
        } catch (TelegramApiException e) {
            log.debug("Sending of message by Marshak was faild! IDIOT!");
        }
    }
}
