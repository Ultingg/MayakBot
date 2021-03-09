package ru.kumkuat.application.GameModule.Service;

import org.springframework.stereotype.Service;
import ru.kumkuat.application.GameModule.Collections.Scene;
import ru.kumkuat.application.GameModule.Factories.SceneFactory;

import java.util.List;

@Service
public class SceneService {
    private List<Scene> SceneCollection;

    public SceneService(SceneFactory sceneFactory) {
        SceneCollection = sceneFactory.getSceneCollection();
    }
}
