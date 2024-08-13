package ru.kumkuat.application.gameModule.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Chat;
import ru.kumkuat.application.gameModule.exceptions.TelegramChatServiceException;
import ru.kumkuat.application.gameModule.models.TelegramChat;
import ru.kumkuat.application.gameModule.repository.TelegramChatRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@PropertySource(value = "file:../resources/externalsecret.yml")
public class TelegramChatService {

    @Value("${admin.chat.id}")
    private String adminChatId;

    private final TelegramChatRepository telegramChatRepository;

    public TelegramChatService(TelegramChatRepository telegramChatRepository) {
        this.telegramChatRepository = telegramChatRepository;
    }

    public String getAdminChatId() {
        return adminChatId;
    }

    public List<TelegramChat> getAll() {
        log.info("getting all chats");
        List<TelegramChat> telegramChatList = new ArrayList<>();
        Iterable<TelegramChat> repoCollection = telegramChatRepository.findAll();
        for (TelegramChat telegramChat : repoCollection) {
            telegramChatList.add(telegramChat);
        }
        log.info("getting all chats finished");
        return telegramChatList;
    }

    public TelegramChat getFreeChat() throws Exception {
        var freeChat = getAll().stream().filter(chat -> !chat.isBusy()).findFirst();
        return freeChat.orElseThrow(Exception::new);
    }

    public ArrayList<TelegramChat> getBusyChats() {
        log.info("Filter busy chats...");
        var busyChats = getAll().stream().filter(chat -> chat.isBusy());

        List<TelegramChat> busyChatsList = busyChats.collect(Collectors.toList());
        log.info("Filter busy chats finished");
        return new ArrayList<>(busyChatsList);
    }

    public TelegramChat getChatById(Long id) throws Exception {
        var telegramChat = getAll().stream().filter(chat -> chat.getChatId().equals(id)).findFirst();
        return telegramChat.orElseThrow(Exception::new);
    }

    public boolean isFreeChatHas() {
        return getAll().stream().anyMatch(chat -> !chat.isBusy());
    }


    private boolean isTelegramChatExist(Long telegramChatId) {
        return telegramChatRepository.existsTelegramChatByChatId(telegramChatId);
    }

    public boolean isTelegramChatExist(TelegramChat telegramChat) {
        if (telegramChat.getId() != null) {
            return getAll().stream().anyMatch(chat -> chat.getId().equals(telegramChat.getId()));
        } else {
            return getAll().stream().anyMatch(chat -> chat.getChatId().equals(telegramChat.getChatId()));
        }
        //Надо отслеживать изменение ссылок
    }

    public boolean isUserAlreadyGetChat(Long userId) {
        return getAll().stream().anyMatch(chat -> chat.getUserId() != null && chat.getUserId() == userId.longValue());
    }

    public long setChatIntoDB(Chat chat) {
        validateChat(chat);
        if (!isTelegramChatExist(chat.getId())) {
            TelegramChat telegramChat = new TelegramChat();
            telegramChat.setBusy(false);
            telegramChat.setName(chat.getTitle());
            telegramChat.setChatId(chat.getId());
            return telegramChatRepository.save(telegramChat).getChatId();
        } else {
                throw new TelegramChatServiceException("Telegram chat already exist!");
            }
    }

    private void validateChat(Chat chat) {
        if (chat.getInviteLink() == null && chat.getTitle().isEmpty()) {
            throw new TelegramChatServiceException("Invite link or chat tittle are empty!");
        }
    }

    public boolean saveChatIntoDB(TelegramChat telegramChat) {
        if (isTelegramChatExist(telegramChat.getChatId())) {
            telegramChatRepository.save(telegramChat);
            return true;
        } else {
            return false;
        }
    }

    public TelegramChat getChatByUserTelegramId(Long telegramUserId) {
        var userchat = getAll().stream().filter(chat -> chat.getUserId() != null
                && telegramUserId.equals(chat.getUserId())).findFirst();
        return userchat.orElseThrow(() ->
                new TelegramChatServiceException("User hasn't assigned chat. User id" + telegramUserId));
    }

    public void cleanChatByUserTelegramId(Long telegramUserId) {
        TelegramChat chat = getChatByUserTelegramId(telegramUserId);
        chat.setBusy(false);
        chat.setUserId(null);
        telegramChatRepository.save(chat);
    }

    public Long getUserTelegramIdByChatId(Long chatId) {
        TelegramChat chat = telegramChatRepository.getTelegramChatByChatId(chatId);
        if (chat != null) return chat.getUserId();
        return null;
    }

    public TelegramChat getChatByTelegramChatId(Long telegramChatId) {
        return telegramChatRepository.getTelegramChatByChatId(telegramChatId);
    }
}
