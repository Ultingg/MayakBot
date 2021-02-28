package ru.kumkuat.application.GameModule.Collections;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.kumkuat.application.GameModule.Geolocation.Geolocation;

@Slf4j
@Setter
@Getter
@Builder
@Component
@NoArgsConstructor
@AllArgsConstructor
public class Reply {
    private Long id;
    private Geolocation geolocation;
    private Long audioId;
    private String textMessage;
    private Long pictureId;
    private int timing;
    private String botName;


    public boolean hasPicture() {
        return pictureId != null;
    }

    public boolean hasGelocation() {
        return geolocation != null;
    }

    public boolean hasAudio() {
        return audioId != null;
    }

    public boolean hasText() {
        return textMessage != null;
    }

}
