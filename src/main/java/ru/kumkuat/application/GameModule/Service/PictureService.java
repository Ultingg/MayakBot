package ru.kumkuat.application.GameModule.Service;

import org.springframework.stereotype.Service;
import org.w3c.dom.Node;
import ru.kumkuat.application.GameModule.Models.Picture;
import ru.kumkuat.application.GameModule.Repository.PictureRepository;

@Service
public class PictureService {

    private final PictureRepository pictureRepository;

    public PictureService(PictureRepository pictureRepository) {
        this.pictureRepository = pictureRepository;
        cleanAll();
    }

    public String getPathToPicture(Long id) {
        Picture picture = pictureRepository.getById(id);
        return picture.getPath();
    }

    public long setPictureIntoDB(Node replyNode) {
        Picture picture = new Picture();
        var path = replyNode.getAttributes().getNamedItem("path").getNodeValue();
        picture.setPath(path);
        pictureRepository.save(picture);
        return picture.getId();
    }

    public void cleanAll() {
        pictureRepository.deleteAll();
    }
}
