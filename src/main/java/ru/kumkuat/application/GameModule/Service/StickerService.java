package ru.kumkuat.application.GameModule.Service;

import org.springframework.stereotype.Service;
import ru.kumkuat.application.GameModule.Models.Sticker;
import ru.kumkuat.application.GameModule.Repository.StickerRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class StickerService {

    private final StickerRepository stickerRepository;

    public StickerService(StickerRepository stickerRepository) {
        this.stickerRepository = stickerRepository;
        cleanAll();
    }


    public Sticker getById(Long id) {
        Sticker sticker = stickerRepository.findById(id).orElseThrow();
        return sticker;
    }

    public List<Sticker> getAll() {
        ArrayList<Sticker> arrayList = new ArrayList<>();
        Iterable<Sticker> stickers = stickerRepository.findAll();
        for (Sticker sticker : stickers) arrayList.add(sticker);
        return arrayList;
    }

    public long setStickerToDB(String path) {
        Sticker sticker = new Sticker();
        sticker.setPath(path);
        Long stickerId = Long.valueOf(getAll().size()) + 1;
        sticker.setId(stickerId);
        stickerRepository.save(sticker);
        return sticker.getId();
    }

    public String getPathToSticker(Long id) {
        Sticker sticker = stickerRepository.findById(id).orElseThrow();
        return sticker.getPath();
    }

    public void cleanAll() {
        stickerRepository.deleteAll();
    }

}
