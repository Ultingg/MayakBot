package ru.kumkuat.application.gameModule.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.kumkuat.application.gameModule.models.Sticker;

@Repository
public interface StickerRepository extends CrudRepository<Sticker, Long> {
}
