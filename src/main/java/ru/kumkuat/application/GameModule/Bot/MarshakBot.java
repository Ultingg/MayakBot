package ru.kumkuat.application.GameModule.Bot;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendLocation;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendVoice;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.kumkuat.application.GameModule.Abstract.TelegramWebhookCommandBot;
import ru.kumkuat.application.GameModule.Commands.MarshakCommands.PayCommand;
import ru.kumkuat.application.GameModule.Commands.MarshakCommands.PlayCommand;


@Setter
@Getter
@Component
@PropertySource(name = "secret.yml", value = "secret.yml")
public class MarshakBot extends TelegramWebhookCommandBot {

    @Value("${marshak.name}")
    private String botUsername;
    @Value("${marshak.token}")
    private String botToken;
    @Value("${marshak.path}")
    private String botPath;

//    @Autowired
//    private PlayCommand playCommand;

    private MarshakBot(PlayCommand playCommand, PayCommand payCommand){
        this.RegisterCommand(playCommand, payCommand);
    }

    public void RegisterCommand(PlayCommand playCommand, PayCommand payCommand){
        register(playCommand);
        register(payCommand);
    }

    @Override
    public BotApiMethod processNonCommandUpdate(Update update) {
        return null;
    }

    public synchronized void sendMsg(String chatId, String s, Integer ID) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(chatId);
        sendMessage.setReplyToMessageId(ID);
        sendMessage.setText(s);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
