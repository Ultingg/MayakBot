package ru.kumkuat.application.gameModule.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.kumkuat.application.gameModule.bot.MarshakBot;
import ru.kumkuat.application.gameModule.exceptions.ChatNotFoundException;
import ru.kumkuat.application.gameModule.models.MessageContainer;
import ru.kumkuat.application.gameModule.models.TelegramChat;

import java.util.List;

/**
 * Class sending force messages to chat from api requests.
 */
@Slf4j
@Service
public class ForceMessageService {


    private final MarshakBot marshakBot;
    private final TelegramChatService chatService;

    public ForceMessageService(MarshakBot marshakBot, TelegramChatService chatService) {
        this.marshakBot = marshakBot;
        this.chatService = chatService;
    }

    /**
     * Method send message to all busy chats.
     *
     * @param messageContainer - container with message.
     */
    public void forceMessageToAllChats(MessageContainer messageContainer) {
        List<TelegramChat> allBusyChats = chatService.getAllByBusy();
        String textMessage = messageContainer.getMessage();

        allBusyChats.stream().map(TelegramChat::getChatId)
                .forEach(id -> sendTextMessageToChat(id, textMessage));
        log.info("Force sending text message to chats finished.");
    }

    /**
     * Method send message to defined chat.
     *
     * @param messageContainer - container with message.
     */
    public String forceMessageToDefinedChat(MessageContainer messageContainer, Long chatId) {
        try {
            TelegramChat chat = chatService.getChatById(chatId);
            String textMessage = messageContainer.getMessage();
            sendTextMessageToChat(chat.getChatId(), textMessage);
        } catch (ChatNotFoundException exception) {
            log.error(exception.getMessage());
            return "FAILED";
        }

        log.info("Force sending text message to chats finished.");
        return "DONE";
    }


    private void sendTextMessageToChat(Long chatId, String textMessage) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(textMessage);
        sendMessage.setChatId(chatId);
        try {
            log.info("Force sending text message to Chat: " + chatId);

            marshakBot.execute(sendMessage);
            log.info("Force  text message to Chat: " + chatId + " has been send.");
        } catch (TelegramApiException e) {
            log.error("Error with force sending text message to Chat: " + chatId);
            log.error(e.getMessage());
        }
    }

}
