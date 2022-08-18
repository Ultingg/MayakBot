package ru.kumkuat.application.gameModule.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.kumkuat.application.gameModule.models.BGUser;

import java.time.LocalTime;

@Repository
public interface BGUserRepository extends CrudRepository<BGUser, Long> {

    BGUser findBGUserByTelegramUserName(String telegramUsername);

    boolean existsBGUserByStartTime(LocalTime startTime);

    boolean existsBGUserByTelegramUserName(String username);

    @Query(value = "SELECT start_time FROM bguser  where telegram_user_name = :username",
            nativeQuery = true)
    LocalTime getTimeByUsername(@Param("username") String username);
}
