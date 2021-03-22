package ru.kumkuat.application.GameModule.Service;

import org.springframework.stereotype.Service;
import org.w3c.dom.Node;
import ru.kumkuat.application.GameModule.Models.Audio;
import ru.kumkuat.application.GameModule.Repository.AudioRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class AudioService {

    private final AudioRepository audioRepository;

    public AudioService(AudioRepository audioRepository) {
        this.audioRepository = audioRepository;
        cleanAll();
    }

    public List<Audio> getAll() {
        List<Audio> audioList = new ArrayList<>();
        Iterable<Audio> repoCollection = audioRepository.findAll();
        for (Audio audio : repoCollection) audioList.add(audio);

        return audioList;
    }

    public String getPathToAudio(Long id) {
        Audio audio = audioRepository.getById(id);
        return audio.getPath();
    }

    public long setAudioIntoDB(Node replyNode) {
        Audio audio = new Audio();
        var path = replyNode.getAttributes().getNamedItem("path").getNodeValue();
        audio.setPath(path);
        Long audiId = Long.valueOf(getAll().size()) + 1;
        audio.setId(audiId);
        audioRepository.save(audio);
        return audio.getId();
    }

    public void cleanAll() {
        audioRepository.deleteAll();
    }
}
