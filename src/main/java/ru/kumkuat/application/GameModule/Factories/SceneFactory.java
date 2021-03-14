package ru.kumkuat.application.GameModule.Factories;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.w3c.dom.Node;
import ru.kumkuat.application.GameModule.Collections.Reply;
import ru.kumkuat.application.GameModule.Collections.Scene;
import ru.kumkuat.application.GameModule.Collections.Trigger;
import ru.kumkuat.application.GameModule.Service.AudioService;
import ru.kumkuat.application.GameModule.Service.GeolocationDatabaseService;
import ru.kumkuat.application.GameModule.Service.PictureService;
import ru.kumkuat.application.GameModule.Service.XMLService;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Component
public class SceneFactory {
    private final XMLService xmlService;
    private final GeolocationDatabaseService geolocationDatabaseService;
    private final PictureService pictureService;
    private final AudioService audioService;

    public SceneFactory(XMLService xmlService, GeolocationDatabaseService geolocationDatabaseService, PictureService pictureService, AudioService audioService) {
        this.xmlService = xmlService;
        this.geolocationDatabaseService = geolocationDatabaseService;
        this.pictureService = pictureService;
        this.audioService = audioService;
    }

    public List<Scene> getSceneCollection() {
        var scenes = new ArrayList<Scene>();
        Integer countScene = xmlService.getCountScene();
        for (int i = 0; i < countScene; i++) {
            //Проверить все ли сцены по порядку
            scenes.add(getScene((long) i));
        }
        return scenes;
    }

    private Scene getScene(Long sceneId) {
        Scene scene = new Scene();
        scene.setTrigger(getTrigger(sceneId));
        scene.setReplyCollection(getReplyCollection(sceneId));
        return scene;
    }

    private Trigger getTrigger(Long sceneId) {
        try {
            Trigger trigger = new Trigger();
            Node triggerNode = xmlService.getTriggerNode(sceneId);

            if (triggerNode != null) {
                if (triggerNode.getNodeName().equals("message")) {
                    trigger.setText(triggerNode.getFirstChild().getNodeValue());
                } else if (triggerNode.getNodeName().equals("location")) {
                    long r = geolocationDatabaseService.setGeolocationIntoDB(triggerNode);
                    trigger.setGeolocationId(r);
                } else if (triggerNode.getNodeName().equals("image")) {
                    trigger.setHasPicture(true);
                }
            } else {
                throw new Exception("Trigger was not found");
            }
            return trigger;
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
        return null;
    }

    private ArrayList<Reply> getReplyCollection(Long sceneId) {
        try {
            ArrayList<Reply> replies = new ArrayList<>();
            var repliesNodes = xmlService.getRepliesNodes(sceneId);
            if (repliesNodes != null) {

                for (var replyNode :
                        repliesNodes) {

                    Reply reply = new Reply();
                    var ContentNode = replyNode.getChildNodes().item(1);

                    var botName = replyNode.getAttributes().getNamedItem("botname").getNodeValue();
                    reply.setBotName(botName);

                    var timing = replyNode.getAttributes().getNamedItem("pause").getNodeValue();
                    reply.setTiming(Integer.parseInt(timing));

                    if (ContentNode.getNodeName().equals("message")) {
                        var msg = ContentNode.getFirstChild().getNodeValue();
                        reply.setTextMessage(msg);
                    } else if (ContentNode.getNodeName().equals("image")) {
                        long r = pictureService.setPictureIntoDB(ContentNode);
                        reply.setPictureId(r);
                    } else if (ContentNode.getNodeName().equals("audio")) {
                        long r = audioService.setAudioIntoDB(ContentNode);
                        reply.setAudioId(r);
                    } else if (ContentNode.getNodeName().equals("location")) {
                        long r = geolocationDatabaseService.setGeolocationIntoDB(ContentNode);
                        reply.setGeolocationId(r);
                    }
                    replies.add(reply);
                }
            } else {
                throw new Exception("Replies list was not found");
            }
            return replies;
        } catch (Exception ex) {
            System.out.println(ex.toString());
        }
        return null;
    }
}
