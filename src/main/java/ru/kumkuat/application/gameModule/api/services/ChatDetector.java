package ru.kumkuat.application.gameModule.api.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.groupadministration.ExportChatInviteLink;
import ru.kumkuat.application.gameModule.bot.MarshakBot;
import ru.kumkuat.application.gameModule.models.TelegramChat;
import ru.kumkuat.application.gameModule.service.TelegramChatService;

@Slf4j
@Service
public class ChatDetector {

    private final MarshakBot marshakBot;
    private final TelegramChatService telegramChatService;

    public ChatDetector(MarshakBot marshakBot, TelegramChatService telegramChatService) {
        this.marshakBot = marshakBot;
        this.telegramChatService = telegramChatService;
    }

    public String detectChatByChatId(Long chatId) {
        log.info("Detecting chat " + chatId);
        try {
            TelegramChat chat = telegramChatService.getChatById(chatId);
            ExportChatInviteLink exportChatInviteLink = new ExportChatInviteLink(chat.getChatId().toString());
            String link = marshakBot.execute(exportChatInviteLink);
            log.info("Chat detected id " + chatId);
            return link;
        } catch (Exception e) {
            log.error("Detect chat Error: " + e.getMessage());
            return "none";
        }
    }

    public String detectChatByTelegramUserId(Long telegramUsrId) {
        log.info("Detecting chat by telegramUserId: " + telegramUsrId);
        try {
            TelegramChat chat = telegramChatService.getChatByUserTelegramId(telegramUsrId);
            ExportChatInviteLink exportChatInviteLink = new ExportChatInviteLink(chat.getChatId().toString());
            String link = marshakBot.execute(exportChatInviteLink);
            log.info("Chat detected by telegramUserId: " + telegramUsrId);
            return link;
        } catch (Exception e) {
            log.error("Detect chat Error: " + e.getMessage());
            return "none";
        }
    }
}
