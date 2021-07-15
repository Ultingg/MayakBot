package ru.kumkuat.application.GameModule.Repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.kumkuat.application.GameModule.Models.BGUser;

import java.time.LocalTime;

@Repository
public interface BGUserRepository extends CrudRepository<BGUser, Long> {


    BGUser findBGUserByTelegramUserName(String telegramUsername);

    BGUser findBGUserByStartTime(LocalTime startTime);

    boolean existsBGUserByStartTime(LocalTime startTime);

    LocalTime findStartTimeByTelegramUserName(String username);
}
