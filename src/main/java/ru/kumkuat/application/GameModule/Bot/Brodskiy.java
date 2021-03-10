package ru.kumkuat.application.GameModule.Bot;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@Data
@Slf4j
@Component
@PropertySource(name = "secret.yml", value = "secret.yml")
public class Brodskiy extends TelegramWebhookBot {

    @Value("${brodskiy.name}")
    private String botUsername;
    @Value("${brodskiy.token}")
    private String botToken;
    @Value("${brodskiy.path}")
    private String BotPath;



    @Override
    public BotApiMethod onWebhookUpdateReceived(Update update) {
        System.out.println("update recieved!");
       Message message =  update.getMessage();
       String chatId = String.valueOf(message.getChat().getId());
       int messageId = message.getMessageId();

        SendMessage sendMessage = new SendMessage();
        sendMessage.setText("Не выходи из комнаты, пиши код!");
        sendMessage.setChatId(chatId);
        sendMessage.setReplyToMessageId(messageId);




        return sendMessage;
    }


    @Override
    public void onRegister() {

    }
}
