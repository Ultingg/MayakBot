package ru.kumkuat.application.GameModule.Commands.MarshakCommands;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.kumkuat.application.GameModule.Bot.MarshakBot;
import ru.kumkuat.application.GameModule.Service.UserService;
import ru.kumkuat.application.GameModule.Service.XLSXBGListReaderService;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

@Slf4j
@Service
public class InputXSLXCommand extends BotCommand {
    private String path = "../resources/input_bg_users.xlsx";
    @Autowired
    private MarshakBot marshakBot;
    @Autowired
    private XLSXBGListReaderService xlsxbgListReaderService;

    private final UserService userService;

    public InputXSLXCommand(UserService userService) {
        super("/input_xslx", "Импорт екселя");
        this.userService = userService;
    }

    @Override
    public void processMessage(AbsSender absSender, Message message, String[] arguments) {

        var document = message.getDocument();
        if (document != null) {
            try {
                var gf = new GetFile();
                gf.setFileId(document.getFileId());
                var file = absSender.execute(gf);
                File localfile = new File(path);
                if (!localfile.exists()) {
                    localfile.createNewFile();
                }
                InputStream is = new URL(file.getFileUrl(marshakBot.getBotToken())).openStream();
                FileUtils.copyInputStreamToFile(is, localfile);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        execute(absSender, message.getFrom(), message.getChat(), arguments);
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {

        long userId = user.getId().longValue();
        if (userId == marshakBot.getId() && userService.IsUserExist(chat.getId())) {
            userId = chat.getId();
        }

        if (userId == chat.getId()) {

            log.debug("Marshak ");
            SendMessage replyMessage = new SendMessage();
            replyMessage.setChatId(chat.getId().toString());
            replyMessage.enableHtml(true);

            try {
                if (!userService.IsUserExist(userId)) {

                } else {
                    xlsxbgListReaderService.XLSXBGParser(path);
                    replyMessage.setText("Пользователи успешно добавлены!");
                    execute(absSender, replyMessage, user);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    void execute(AbsSender sender, SendMessage message, User user) {
        try {
            sender.execute(message);
        } catch (TelegramApiException e) {
        }
    }
}
