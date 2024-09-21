package ru.kumkuat.application.gameModule.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.kumkuat.application.gameModule.models.TelegramChat;

import java.util.List;

@Repository
public interface TelegramChatRepository extends CrudRepository<TelegramChat, Long> {
    TelegramChat getById(Long id);

    TelegramChat getTelegramChatByChatId(Long id);

    boolean existsTelegramChatByChatId(Long id);

    @Query("SELECT t FROM TelegramChat t WHERE t.isBusy = :busy")
    List<TelegramChat> getAllChatsByBusy(@Param("busy")boolean busy);
}
