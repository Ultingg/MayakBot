package ru.kumkuat.application.gameModule.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.kumkuat.application.gameModule.models.User;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    User getById(Long id);


//    @Query("SELECT u FROM User u where u.telegramUserId = ?1")
//    User getByTelegramUserId(Long id);

    User getByTelegramUserId(Long id);

}
