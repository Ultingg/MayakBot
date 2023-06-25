package ru.kumkuat.application.gameModule.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.kumkuat.application.gameModule.models.Picture;

@Repository
public interface PictureRepository extends CrudRepository<Picture, Long> {
    Picture getById(Long id);
}
