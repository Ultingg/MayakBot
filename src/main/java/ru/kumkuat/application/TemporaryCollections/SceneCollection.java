package ru.kumkuat.application.TemporaryCollections;

import org.springframework.stereotype.Component;
import ru.kumkuat.application.GameModule.Collections.Reply;
import ru.kumkuat.application.GameModule.Collections.Scene;
import ru.kumkuat.application.GameModule.Collections.Trigger;

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
        setUpCollection();


    }

    private void setUpCollection() {
        // временно насели список сценами
        Scene scene = new Scene();
        Trigger trigger = new Trigger();
        trigger.setText("тут,здесь,на месте,наместе,тута,пришел");

        Reply reply1 = Reply.builder()
                .textMessage("Ну здраствуй, поэт!")
                .botName("Mayakovsky")
                .timing(1000)
                .build();
        Reply reply2 = Reply.builder()
                .botName("Akhmatova")
                .textMessage("Не обрайщай на него внимания он дурачится.")
                .timing(2000)
                .build();
        Reply reply3 = Reply.builder()
                .botName("Akhmatova")
                .textMessage("Как вас зовут, дорогой друг?")
                .timing(5000)
                .build();
        Reply reply4 = Reply.builder()
                .botName("Brodskiy")
                .textMessage("Я тут главный!")
                .timing(2000)
                .build();
        Reply reply5 = Reply.builder()
                .botName("Ku")
                .geolocationId(1L)
                .timing(3500)
                .build();
        Reply reply6 = Reply.builder()
                .botName("Brodskiy")
                .pictureId(1L)
                .timing(2000)
                .build();
        Reply reply7 = Reply.builder()
                .botName("Ku")
                .audioId(1L)
                .timing(3500)
                .build();

        ArrayList<Reply> replyArrayList = new ArrayList<>();
        replyArrayList.add(reply1);
        replyArrayList.add(reply2);
        replyArrayList.add(reply3);
        replyArrayList.add(reply4);
        replyArrayList.add(reply5);
        replyArrayList.add(reply6);
        replyArrayList.add(reply7);

        scene.setTrigger(trigger);
        scene.setReplyCollection(replyArrayList);

        sceneList.add(scene);
    }


}
