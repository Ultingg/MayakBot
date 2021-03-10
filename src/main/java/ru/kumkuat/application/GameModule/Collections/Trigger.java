package ru.kumkuat.application.GameModule.Collections;

import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class Trigger {

    private Long id;
    private String text;
    private boolean hasPicture;
    private Long geolocationId;
}
