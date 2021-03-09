package ru.kumkuat.application.GameModule.Collections;

import lombok.Data;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

@Data
@Component
public class Trigger {

    private Long id;
    private String text;
    private boolean hasPicture;
    private Long geolocationId;




    public boolean triggerCheck(Message message) {
        boolean userPicture = message.getPhoto() != null;
        return userPicture == hasPicture;
    }


}
