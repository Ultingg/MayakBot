package ru.kumkuat.application.gameModule.collections;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Setter
@Getter
@Builder
@Component
@NoArgsConstructor
@AllArgsConstructor
public class Reply {
    private Long geolocationId;
    private Long audioId;
    private String textMessage;
    private Long pictureId;
    private int timing;
    private String botName;
    private Long stickerId;
    private PinnedMessage pinnedMessage;


    public boolean hasPicture() {
        return pictureId != null;
    }

    public boolean hasGeolocation() {
        return geolocationId != null;
    }

    public boolean hasAudio() {
        return audioId != null;
    }

    public boolean hasText() {
        return textMessage != null;
    }

    public boolean hasSticker() {
        return stickerId != null;
    }

    public boolean hasPinnedMessage() {
        return pinnedMessage != null;
    }

}
