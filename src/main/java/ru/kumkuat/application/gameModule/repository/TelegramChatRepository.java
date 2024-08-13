package ru.kumkuat.application.gameModule.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.kumkuat.application.gameModule.models.TelegramChat;

@Repository
public interface TelegramChatRepository extends CrudRepository<TelegramChat, Long> {
    TelegramChat getById(Long id);

    TelegramChat getTelegramChatByChatId(Long id);

    boolean existsTelegramChatByChatId(Long id);
}
