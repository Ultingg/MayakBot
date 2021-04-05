package ru.kumkuat.application.GameModule.Service;

import org.springframework.stereotype.Service;
import ru.kumkuat.application.GameModule.Collections.Scene;
import ru.kumkuat.application.GameModule.Factories.SceneFactory;

import java.util.List;

@Service
public class SceneService {
    private final List<Scene> sceneCollection;

    public SceneService(SceneFactory sceneFactory) {
        sceneCollection = sceneFactory.getSceneCollection();
    }

    public int count() {
        return sceneCollection.size();
    }

    public Scene getScene (Long id) {
      return   sceneCollection.get(Math.toIntExact(id));
    }
}
