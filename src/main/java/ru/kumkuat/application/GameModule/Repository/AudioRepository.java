package ru.kumkuat.application.GameModule.Repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.kumkuat.application.GameModule.Models.Audio;

@Repository
public interface AudioRepository extends CrudRepository<Audio, Long> {

     Audio getById(Long id);
}
