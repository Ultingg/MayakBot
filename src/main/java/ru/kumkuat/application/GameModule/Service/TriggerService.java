package ru.kumkuat.application.GameModule.Service;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Location;
import ru.kumkuat.application.GameModule.Collections.Trigger;
import ru.kumkuat.application.GameModule.Models.Geolocation;

@Service
public class TriggerService {
    private final GeolocationDatabaseService geolocationDatabaseService;

    public TriggerService(GeolocationDatabaseService geolocationDatabaseService) {
        this.geolocationDatabaseService = geolocationDatabaseService;
    }

    public boolean triggerCheck(Trigger trigger, String textToCheck) {
            String[] stringsFromTrigger = trigger.getText().split(",");
            boolean flag = false;
            for (String string : stringsFromTrigger) {
                if(string.toLowerCase().equals(textToCheck.toLowerCase())) {
                    flag = true;
                    break;
                }
            }
            return flag;
//            return   textToCheck.toLowerCase().contains(trigger.getText().toLowerCase());
    }

    public boolean triggerCheck(Trigger trigger, Location userLocation) {
        double userLat = userLocation.getLatitude();
        double userLong = userLocation.getLongitude();
        Long geolocationId = trigger.getGeolocationId();
        Geolocation geolocation = geolocationDatabaseService.getGeolocationById(geolocationId);
        double distance = GeoLocationUtilsService.distanceToCurrentLocation(userLat, userLong, geolocation.getLatitude(), geolocation.getLongitude());
        return distance <= 50.0;
    }
  }
