package ru.kumkuat.application.gameModule.service.resources;

import org.springframework.stereotype.Service;
import org.w3c.dom.Node;
import ru.kumkuat.application.gameModule.models.Picture;
import ru.kumkuat.application.gameModule.repository.PictureRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class PictureService {

    private final PictureRepository pictureRepository;

    public PictureService(PictureRepository pictureRepository) {
        this.pictureRepository = pictureRepository;
        cleanAll();
    }

    public List<Picture> getAll() {
        List<Picture> pictureList = new ArrayList<>();
        Iterable<Picture> repoCollection = pictureRepository.findAll();
        for (Picture pic : repoCollection) pictureList.add(pic);

        return pictureList;
    }

    public String getPathToPicture(Long id) {
        Picture picture = pictureRepository.getById(id);
        return picture.getPath();
    }

    public long setPictureIntoDB(Node replyNode) {
        var path = replyNode.getAttributes().getNamedItem("path").getNodeValue();
        return setPictureIntoDB(path);
    }

    public long setPictureIntoDB(String path) {
        Picture picture = new Picture();
        picture.setPath(path);
        Long picId = Long.valueOf(getAll().size()) + 1; // костыль
        picture.setId(picId);
        pictureRepository.save(picture);
        return picture.getId();
    }

    public void cleanAll() {
        pictureRepository.deleteAll();
    }
}
