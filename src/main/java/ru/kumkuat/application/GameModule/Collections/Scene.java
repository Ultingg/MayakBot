package ru.kumkuat.application.GameModule.Collections;


import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Setter
@Getter
@Builder
@Component
@NoArgsConstructor
@AllArgsConstructor
public class Scene {

    private List<Reply> replyCollection;
    private Trigger trigger;

    public boolean insertReply(Reply reply) {
        boolean result = false;
        if (replyCollection != null) {
            result = replyCollection.add(reply);
        } else {
            replyCollection = new ArrayList<>();
            result = replyCollection.add(reply);
        }
        return result;
    }


}
