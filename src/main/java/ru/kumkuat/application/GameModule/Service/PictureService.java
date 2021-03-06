package ru.kumkuat.application.GameModule.Service;

import org.springframework.stereotype.Service;
import ru.kumkuat.application.GameModule.Models.Picture;
import ru.kumkuat.application.GameModule.Repository.PictureRepository;

@Service
public class PictureService {

    private final PictureRepository pictureRepository;

    public PictureService(PictureRepository pictureRepository) {
        this.pictureRepository = pictureRepository;
    }

    public String getPathToPicture(Long id) {
        Picture picture = pictureRepository.getById(id);
        return picture.getPath();
    }
}
