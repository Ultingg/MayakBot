package ru.kumkuat.application.GameModule.Repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.kumkuat.application.GameModule.Models.TelegramChat;

@Repository
public interface TelegramChatRepository extends CrudRepository<TelegramChat, Long> {
    TelegramChat getById(Long id);
}
