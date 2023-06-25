package ru.kumkuat.application.gameModule.collections;

import lombok.Data;
import org.springframework.stereotype.Component;

@Data
@Component
public class Trigger {

    private Long id;
    private String text;
    private boolean hasPicture;
    private Long geolocationId;
    private boolean hasGeolocation = false;

    public void setGeolocationId(Long geolocationId) {
        this.geolocationId = geolocationId;
        this.hasGeolocation = true;
    }
}
