package ru.kumkuat.application.TemporaryCollections;

import org.springframework.stereotype.Component;
import ru.kumkuat.application.GameModule.Collections.Scene;

import java.util.ArrayList;
import java.util.List;

@Component
public class SceneCollection {


    private List<Scene> sceneList;

    public Scene get(Long id) {
        return sceneList.get(Math.toIntExact(id));
    }

    public SceneCollection() {
        sceneList = new ArrayList<>();
    }


}
