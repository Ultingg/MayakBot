package ru.kumkuat.application.GameModule.Service;

import org.springframework.stereotype.Service;
import ru.kumkuat.application.GameModule.Models.Audio;
import ru.kumkuat.application.GameModule.Repository.AudioRepository;

@Service
public class AudioService {

   private final AudioRepository audioRepository;

    public AudioService(AudioRepository audioRepository) {
        this.audioRepository = audioRepository;
    }

    public String getPathToAudio(Long id) {
       Audio audio = audioRepository.getById(id);
       return audio.getPath();
    }

}
