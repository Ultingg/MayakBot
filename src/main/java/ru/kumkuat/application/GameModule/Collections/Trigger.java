package ru.kumkuat.application.GameModule.Collections;

import lombok.Data;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Location;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.kumkuat.application.GameModule.Geolocation.GeoLocationUtils;
import ru.kumkuat.application.GameModule.Geolocation.Geolocation;

@Data
@Component
public class Trigger {

    private Long id;
    private String text;
    private boolean hasPicture;
    private Geolocation geolocation;


    public boolean triggerCheck(String textToCheck) {
        return text.equals(textToCheck.toLowerCase());
    }

    public boolean triggerCheck(Location location){
        double userLat = location.getLatitude();
        double userLong = location.getLongitude();
        double distance = GeoLocationUtils.distanceToCurrentLocation(userLat, userLong, geolocation.getLatitude(), geolocation.getLongitude());
        return distance <=50.0;
    }
    public boolean triggerCheck(Message message) {
        boolean userPicture = message.getPhoto() != null;
        return userPicture == hasPicture;
    }


}
