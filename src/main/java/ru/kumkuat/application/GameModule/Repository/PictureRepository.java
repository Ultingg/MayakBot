package ru.kumkuat.application.GameModule.Repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.kumkuat.application.GameModule.Models.Picture;

@Repository
public interface PictureRepository extends CrudRepository<Picture, Long> {

    Picture getById(Long id);
}
