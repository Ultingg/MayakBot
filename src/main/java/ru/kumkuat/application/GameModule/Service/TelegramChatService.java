package ru.kumkuat.application.GameModule.Service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.kumkuat.application.GameModule.Exceptions.TelegramChatServiceException;
import ru.kumkuat.application.GameModule.Models.TelegramChat;
import ru.kumkuat.application.GameModule.Repository.TelegramChatRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@PropertySource(name = "secret.yml", value = "secret.yml")
public class TelegramChatService {

    @Value("${admin.chat.id}")
    private  String AdminChatId;

    private final TelegramChatRepository telegramChatRepository;

    public TelegramChatService(TelegramChatRepository telegramChatRepository) {
        this.telegramChatRepository = telegramChatRepository;
        //cleanAll();
    }

    public String getAdminChatId() {
        return AdminChatId;
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

    public ArrayList<TelegramChat> getBusyChats() {
        var busyChats = getAll().stream().filter(chat -> chat.isBusy());
        List<TelegramChat> busyChatsList = busyChats.collect(Collectors.toList());
        return new ArrayList<TelegramChat>(busyChatsList);
    }

    public TelegramChat getChatById(Long id) throws Exception {
        var telegramChat = getAll().stream().filter(chat -> chat.getChatId().equals(id)).findFirst();
        return telegramChat.orElseThrow(Exception::new);
    }

    public boolean isFreeChatHas() {
        return getAll().stream().anyMatch(chat -> !chat.isBusy());
    }

    public boolean isTelegramChatExist(TelegramChat telegramChat) {
        if (telegramChat.getId() != null) {
            return getAll().stream().anyMatch(chat -> chat.getId().equals(telegramChat.getId()));
        } else {
            return getAll().stream().anyMatch(chat -> chat.getChatId().equals(telegramChat.getChatId()));
        }
        //Надо отслеживать изменение ссылок
    }

    public boolean isUserAlreadyPlaying(User user) {
        return getAll().stream().anyMatch(chat -> chat.getUserId() != null && chat.getUserId() == user.getId().longValue());
    }

    public long setChatIntoDB(Chat chat) throws Exception {
        if (chat.getInviteLink() != null && !chat.getTitle().isEmpty()) {
            TelegramChat telegramChat = new TelegramChat();
            telegramChat.setBusy(false);
            telegramChat.setName(chat.getTitle());
            telegramChat.setChatId(chat.getId());
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

    public TelegramChat getChatByUserTelegramId(Long telegramUserId) throws Exception {
        var userchat = getAll().stream().filter(chat -> chat.getUserId().equals(telegramUserId)).findFirst();
        return userchat.orElseThrow(Exception::new);
    }
}
