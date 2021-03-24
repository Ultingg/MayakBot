package ru.kumkuat.application.GameModule.Factories;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.w3c.dom.Node;
import ru.kumkuat.application.GameModule.Collections.Reply;
import ru.kumkuat.application.GameModule.Collections.Scene;
import ru.kumkuat.application.GameModule.Collections.Trigger;
import ru.kumkuat.application.GameModule.Exceptions.RepliesEmptyException;
import ru.kumkuat.application.GameModule.Exceptions.RepliesNotFoundException;
import ru.kumkuat.application.GameModule.Exceptions.TriggerEmptyException;
import ru.kumkuat.application.GameModule.Exceptions.TriggerNotFoundException;
import ru.kumkuat.application.GameModule.Service.AudioService;
import ru.kumkuat.application.GameModule.Service.GeolocationDatabaseService;
import ru.kumkuat.application.GameModule.Service.PictureService;
import ru.kumkuat.application.GameModule.Service.XMLService;

import java.util.ArrayList;
import java.util.List;
@Slf4j
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

        try {
            var scenesNodes = xmlService.getSceneNodes();
            for (var sceneNode :
                    scenesNodes) {
                var TriggerNode = xmlService.getTriggerNode(sceneNode);
                var RepliesNode = xmlService.getRepliesNodes(sceneNode);
                Scene scene = new Scene();
                scene.setTrigger(getTrigger(TriggerNode));
                scene.setReplyCollection(getReplyCollection(RepliesNode));
                scenes.add(scene);
            }
        } catch (TriggerNotFoundException ex) {
            ex.getMessage();
            log.debug("trigger not found.");
        } catch (RepliesNotFoundException ex) {
            ex.getMessage();
            log.debug("Reply not found.");
        } catch (RepliesEmptyException ex) {
            ex.getMessage();
            log.debug("Reply is empty.");
        } catch (TriggerEmptyException ex){
            ex.getMessage();
            log.debug("Trigger is empty.");
        } catch (NullPointerException ex) {
            ex.getMessage();
            log.debug("Reply collection is null.");
        }
        log.debug("Scene Collection created.");
        return scenes;
    }

    private Trigger getTrigger(Node triggerNode) throws TriggerEmptyException {
        Trigger trigger = new Trigger();
        if (triggerNode.getNodeName().equals("message")) {
            trigger.setText(triggerNode.getFirstChild().getNodeValue());
        } else if (triggerNode.getNodeName().equals("location")) {
            long r = geolocationDatabaseService.setGeolocationIntoDB(triggerNode);
            trigger.setGeolocationId(r);
        } else if (triggerNode.getNodeName().equals("image")) {
            trigger.setHasPicture(true);
        } else {
            throw new TriggerEmptyException("EXCEPTION: Trigger is empty");
        }
        return trigger;
    }

    private ArrayList<Reply> getReplyCollection(ArrayList<Node> repliesNodes) throws NullPointerException, TriggerEmptyException {
        ArrayList<Reply> replies = new ArrayList<Reply>();
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
                } else {
                    throw new TriggerEmptyException("EXCEPTION: Reply is empty");
                }
                replies.add(reply);
                log.debug("Reply Collection created.");
            }
        } else {
            throw new NullPointerException("EXCEPTION: Replies cannot was nullable");
        }
        return replies;
    }
}
