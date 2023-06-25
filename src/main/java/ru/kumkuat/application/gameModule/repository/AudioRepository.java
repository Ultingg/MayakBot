package ru.kumkuat.application.gameModule.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.kumkuat.application.gameModule.models.Audio;

@Repository
public interface AudioRepository extends CrudRepository<Audio, Long> {
    Audio getById(Long id);


}
