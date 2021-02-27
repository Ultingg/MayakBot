package ru.kumkuat.application.GameModule.Bot;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@Setter
@Getter
@Component
@NoArgsConstructor
@AllArgsConstructor
@PropertySource(name = "secret.yml", value = "secret.yml" )
public class KuBot extends TelegramLongPollingBot {

    @Value("${ku.name}")
    private String botUsername;
    @Value("${ku.token}")
    private String botToken;

    @Override
    public void onUpdateReceived(Update update) {

    }

    public synchronized void sendMsg(String chatId, String s) {
        SendMessage sendMessage = SendMessage.builder().chatId(chatId).text(s).build();
        sendMessage.enableMarkdown(true);
        System.out.println(chatId);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.getStackTrace();
        }
    }
}
