package ru.kumkuat.application.GameModule.Repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.kumkuat.application.GameModule.Models.Audio;

@Repository
public interface AudioRepository extends CrudRepository<Audio, Long> {
    Audio getById(Long id);

//    @Query("CREATE TABLE user (" +
//            "id BIGINT AUTO_INCREMENT NOT NULL," +
//            "name VARCHAR(255)," +
//            "scene_id INT NOT NULL," +
//            "telegram_user_id BIGINT UNIQUE," +
//            " PRIMARY KEY (id));")
//    void setUp();
}
