package ru.kumkuat.application.GameModule.Repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.kumkuat.application.GameModule.Models.Sticker;

@Repository
public interface StickerRepository extends CrudRepository<Sticker, Long> {
}
