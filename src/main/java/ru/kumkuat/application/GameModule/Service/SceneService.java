package ru.kumkuat.application.GameModule.Service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import ru.kumkuat.application.GameModule.Collections.Scene;
import ru.kumkuat.application.GameModule.Factories.SceneFactory;

import java.util.List;

@Service
@PropertySource(value = "file:../resources/externalsecret.yml")
public class SceneService {
    private final List<Scene> sceneCollection;
    private final XLSXReaderService xlsxReaderService;
    @Value("${text.path}")
    private String str;
    public SceneService(SceneFactory sceneFactory, XLSXReaderService xlsxReaderService) {
        this.xlsxReaderService = xlsxReaderService;
        System.out.println("str " +str);
//        sceneCollection = sceneFactory.getSceneCollection();
        sceneCollection = xlsxReaderService.parseXLXS();
    }

    public int count() {
        return sceneCollection.size();
    }

    public Scene getScene (Long id) {
      return   sceneCollection.get(Math.toIntExact(id));
    }

    public boolean addScene(Scene scene) {
       return sceneCollection.add(scene);
    }
}
