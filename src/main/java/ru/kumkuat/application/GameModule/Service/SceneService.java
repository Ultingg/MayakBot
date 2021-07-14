package ru.kumkuat.application.GameModule.Service;

import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import ru.kumkuat.application.GameModule.Collections.Scene;

import java.util.List;

@Service
@PropertySource(value = "file:../resources/externalsecret.yml")
public class SceneService {
    private final List<Scene> sceneCollection;
    private final XLSXScenarioReaderService xlsxScenarioReaderService;

    public SceneService(XLSXScenarioReaderService xlsxScenarioReaderService) {
        this.xlsxScenarioReaderService = xlsxScenarioReaderService;
        sceneCollection = xlsxScenarioReaderService.parseXLXS();
    }

    public int count() {
        return sceneCollection.size();
    }

    public Scene getScene(Long id) {
        return sceneCollection.get(Math.toIntExact(id));
    }

    public boolean addScene(Scene scene) {
        return sceneCollection.add(scene);
    }
}
