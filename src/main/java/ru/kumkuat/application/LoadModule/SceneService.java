package ru.kumkuat.application.LoadModule;

import ru.kumkuat.application.GameModule.Collections.Scene;

import java.util.List;

public class SceneService {
    private List<Scene> SceneCollection = null;
    public SceneService(SceneFactory sceneFactory){
        SceneCollection = sceneFactory.getSceneCollection();
    }
}
