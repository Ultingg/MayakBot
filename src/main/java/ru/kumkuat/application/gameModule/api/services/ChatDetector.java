package ru.kumkuat.application.gameModule.api.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.kumkuat.application.gameModule.bot.MarshakBot;

@Slf4j
@Service
public class ChatDetector {

    private final MarshakBot marshakBot;


    public ChatDetector(MarshakBot marshakBot) {
        this.marshakBot = marshakBot;
    }

    public void detectChat(Long chatId) {
        log.info("Detecting chat " + chatId);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("Chat detected: " + chatId);
        marshakBot.sendMessage(sendMessage);
        log.info("Chat detected id " + chatId);
    }
}
