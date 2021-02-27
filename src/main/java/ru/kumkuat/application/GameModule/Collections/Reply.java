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

    private int id;
    private Geolocation geolocation;
    private int audioId;
    private String textMessage;
    private int pictureId;

}
