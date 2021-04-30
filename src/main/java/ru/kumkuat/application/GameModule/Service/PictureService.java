package ru.kumkuat.application.GameModule.Service;

import org.springframework.stereotype.Service;
import org.w3c.dom.Node;
import ru.kumkuat.application.GameModule.Models.Picture;
import ru.kumkuat.application.GameModule.Repository.PictureRepository;

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
        Picture picture = new Picture();
        var path = replyNode.getAttributes().getNamedItem("path").getNodeValue();
        picture.setPath(path);
        Long picId = Long.valueOf(getAll().size()) + 1; // костыль
        picture.setId(picId);
        pictureRepository.save(picture);
        return picture.getId();
    }

    public long setPictureIntoDB(String picturePath) {
        Picture picture = new Picture();
        picture.setPath(picturePath);
        Long picId = Long.valueOf(getAll().size()) + 1; // костыль
        picture.setId(picId);
        pictureRepository.save(picture);
        return picId;
    }

    public void cleanAll() {
        pictureRepository.deleteAll();
    }
}
