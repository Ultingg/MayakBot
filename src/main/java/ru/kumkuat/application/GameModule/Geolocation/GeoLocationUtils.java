package ru.kumkuat.application.GameModule.Geolocation;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Location;
import ru.kumkuat.application.GameModule.Service.GeolocationService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Math.*;

@Service
public class GeoLocationUtils {

    private final GeolocationService geolocationService;


    public GeoLocationUtils(GeolocationService geolocationService) {
        this.geolocationService = geolocationService;
    }

    public  double distanceToCurrentLocation(Double userLati, Double userLong, Double aimLati, Double aimLong) {
        double EARTH_RADIUS = 6372795.0;

        //переводим координаты в радианы
        double latUser = userLati * PI / 180;
        double longUser = userLong * PI / 180;
        double latAim = aimLati * PI / 180;
        double longAim = aimLong * PI / 180;

        // косинусы и синусы широт и разницы долгот
        double cl1 = cos(latUser);
        double sl1 = sin(latUser);
        double cl2 = cos(latAim);
        double sl2 = sin(latAim);
        double deltaLong = longAim - longUser;
        double cosDelta = cos(deltaLong);
        double sinDelta = sin(deltaLong);

        //вычисление длины большого круга
        double y = Math.sqrt(pow(cl2 * sinDelta, 2) + pow(cl1 * sl2 - sl1 * cl2 * cosDelta, 2));
        double x = sl1 * sl2 + cl1 * cl2 * cosDelta;

        double ad = Math.atan2(y, x);
        return ad * EARTH_RADIUS;
    }


    public  Geolocation nearestLocation(List<Geolocation> geolocationList, Location userLocation) {
        double userLong = userLocation.getLongitude();
        double userLat = userLocation.getLatitude();
        double min = 99999999.0;
        String fullName = "";
        double resultLatitude = 0.0;
        double resulLongitude = 0.0;
        for (Geolocation entry : geolocationList) {

            double longList = entry.getLongitude();
            double latList = entry.getLatitude();
            double temp = distanceToCurrentLocation(userLat, userLong, latList, longList);
            if (temp < min) {
                min = temp;
                fullName = entry.getFullName();
                resulLongitude = longList;
                resultLatitude = latList;
            }
        }
        Geolocation result = new Geolocation();
        result.setFullName(fullName);
        result.setLatitude(resultLatitude);
        result.setLongitude(resulLongitude);
        return result;
    }


    public Map<String, Object> foundNearestLocationService(Location userLocation) {
        double distance;
        Map<String, Object> resultList = new HashMap<>();
        List<Geolocation> geolocationList1 = geolocationService.getAll();
        Geolocation nearestGeolocation = nearestLocation(geolocationList1, userLocation);
        distance = distanceToCurrentLocation(userLocation.getLatitude(), userLocation.getLongitude(), nearestGeolocation.getLatitude(), nearestGeolocation.getLongitude());

        resultList.put("Geolocation", nearestGeolocation);
        resultList.put("Distance", distance);

        return resultList;

    }
}


