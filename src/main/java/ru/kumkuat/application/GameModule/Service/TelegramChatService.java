package ru.kumkuat.application.GameModule.Service;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Chat;
import ru.kumkuat.application.GameModule.Exceptions.TelegramChatServiceException;
import ru.kumkuat.application.GameModule.Models.TelegramChat;
import ru.kumkuat.application.GameModule.Repository.TelegramChatRepository;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.ArrayList;
import java.util.List;

@Service
public class TelegramChatService {

    private final TelegramChatRepository telegramChatRepository;

    public TelegramChatService(TelegramChatRepository telegramChatRepository) {
        this.telegramChatRepository = telegramChatRepository;
        //cleanAll();
    }

    public List<TelegramChat> getAll() {
        List<TelegramChat> telegramChatList = new ArrayList<>();
        Iterable<TelegramChat> repoCollection = telegramChatRepository.findAll();
        for (TelegramChat telegramChat : repoCollection) {
            telegramChatList.add(telegramChat);
        }
        return telegramChatList;
    }

    public TelegramChat getFreeChat() throws Exception {
        var freeChat = getAll().stream().filter(chat -> !chat.isBusy()).findFirst();
        return freeChat.orElseThrow(Exception::new);
    }

    public TelegramChat getChatById(Long id) throws Exception {
        var telegramChat = getAll().stream().filter(chat -> chat.getId() == id).findFirst();
        return telegramChat.orElseThrow(Exception::new);
    }

    public boolean isFreeChatHas() {
        return getAll().stream().anyMatch(chat -> !chat.isBusy());
    }

    public boolean isTelegramChatExist(TelegramChat telegramChat) {
        if (telegramChat.getId() != null) {
            return getAll().stream().anyMatch(chat -> chat.getId().equals(telegramChat.getId()));
        } else {
            return getAll().stream().anyMatch(chat -> chat.getName().equals(telegramChat.getName()) && chat.getInviteLink().equals(telegramChat.getInviteLink()));
        }
        //Надо отслеживать изменение ссылок
    }

    public boolean isUserAlreadyPlaying(User user) {
        return getAll().stream().anyMatch(chat ->chat.getUserId() != null && chat.getUserId() == user.getId().longValue());
    }

    public long setChatIntoDB(Chat chat) throws Exception {
        if (chat.getInviteLink() != null && !chat.getTitle().isEmpty()) {
            TelegramChat telegramChat = new TelegramChat();
            telegramChat.setBusy(false);
            telegramChat.setInviteLink(chat.getInviteLink());
            telegramChat.setName(chat.getTitle());
            if (!isTelegramChatExist(telegramChat)) {
                Long ChatId = Long.valueOf(getAll().size()) + 1;
                telegramChat.setId(ChatId);
                telegramChatRepository.save(telegramChat);
                return telegramChat.getId();
            } else {
                throw new TelegramChatServiceException("Telegram chat already exist!");
            }
        }
        throw new TelegramChatServiceException("Invite link or chat tittle are empty!");
    }

    public boolean saveChatIntoDB(TelegramChat telegramChat) throws Exception {
        if (isTelegramChatExist(telegramChat)) {
            telegramChatRepository.save(telegramChat);
            return true;
        } else {
            return false;
        }
    }

    public void cleanAll() {
        telegramChatRepository.deleteAll();
    }
}