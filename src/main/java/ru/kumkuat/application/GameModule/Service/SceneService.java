package ru.kumkuat.application.GameModule.Service;

import org.springframework.stereotype.Service;
import ru.kumkuat.application.TemporaryCollections.SceneCollection;

@Service
public class SceneService {

    private final SceneCollection sceneCollection;


    public SceneService(SceneCollection sceneCollection) {
        this.sceneCollection = sceneCollection;
    }
}
