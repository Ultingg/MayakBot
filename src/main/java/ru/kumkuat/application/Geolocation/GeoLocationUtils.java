package ru.kumkuat.application.Geolocation;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Location;
import ru.kumkuat.application.Service.GeolocationService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Math.*;
import static java.lang.Math.pow;

@Service
public class GeoLocationUtils {

    private final GeolocationService geolocationService;


    public GeoLocationUtils(GeolocationService geolocationService) {
        this.geolocationService = geolocationService;
    }

    public  Double distanceToCurrentLocation(Double userLati, Double userLong, Double aimLati, Double aimLong) {
        Double EARTH_RADIUS = 6372795.0;

        //переводим координаты в радианы
        Double latUser = userLati * PI / 180;
        Double longUser = userLong * PI / 180;
        Double latAim = aimLati * PI / 180;
        Double longAim = aimLong * PI / 180;

        // косинусы и синусы широт и разницы долгот
        Double cl1 = cos(latUser);
        Double sl1 = sin(latUser);
        Double cl2 = cos(latAim);
        Double sl2 = sin(latAim);
        Double deltaLong = longAim - longUser;
        Double cosDelta = cos(deltaLong);
        Double sinDelta = sin(deltaLong);

        //вычисление длины большого круга
        Double y = Math.sqrt(pow(cl2 * sinDelta, 2) + pow(cl1 * sl2 - sl1 * cl2 * cosDelta, 2));
        Double x = sl1 * sl2 + cl1 * cl2 * cosDelta;

        Double ad = Math.atan2(y, x);
        return ad * EARTH_RADIUS;
    }


    public  Geolocation nearestLocation(List<Geolocation> geolocationList, Location userLocation) {
        Double userLong = userLocation.getLongitude();
        Double userLat = userLocation.getLatitude();
        Double min = 99999999.0;
        String fullName = "";
        Double resultLatitude = 0.0;
        Double resulLongitude = 0.0;
        for (Geolocation entry : geolocationList) {

            Double longList = entry.getLongitude();
            Double latList = entry.getLatitude();
            Double temp = distanceToCurrentLocation(userLat, userLong, latList, longList);
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
        Double distance;
        Map<String, Object> resultList = new HashMap<>();
        List<Geolocation> geolocationList1 = geolocationService.getAll();
        Geolocation nearestGeolocation = nearestLocation(geolocationList1, userLocation);
        distance = distanceToCurrentLocation(userLocation.getLatitude(), userLocation.getLongitude(), nearestGeolocation.getLatitude(), nearestGeolocation.getLongitude());

        resultList.put("Geolocation", nearestGeolocation);
        resultList.put("Distance", distance);

        return resultList;

    }
}

//
//    public static Map<String, Location> locationMap = new HashMap<>();
//    public static List<Geolocation> geolocationList = new ArrayList<>();


//    static {
//        Location petroKrepost = new Location();
//        petroKrepost.setLatitude(59.950157);
//        petroKrepost.setLongitude(30.315352);
//        Location zimniy = new Location();
//        zimniy.setLatitude(59.939916);
//        zimniy.setLongitude(30.314699);
//        Location moscowRailwaySt = new Location();
//        moscowRailwaySt.setLatitude(59.930102);
//        moscowRailwaySt.setLongitude(30.362520);
//        locationMap.put("PetroKrepost", petroKrepost);
//        locationMap.put("Zimniy", zimniy);
//        locationMap.put("MoscowRailwaySt", moscowRailwaySt);
//
//
//        Geolocation petro = new Geolocation();
//        petro.setLatitude(59.950157);
//        petro.setLongitude(30.315352);
//        petro.setFullName("PetroKrepost");
//        Geolocation zimniyD = new Geolocation();
//        zimniyD.setFullName("Zimniy");
//        zimniyD.setLongitude(30.314699);
//        zimniyD.setLatitude(59.939916);
//
//        geolocationList.add(petro);
//        geolocationList.add(zimniyD);
//
//
//    }

//    public static Map<String, Object> foundNearestLocation(Location userLocation) {
//        Double distance = 0.0;
//        Map<String, Object> resultList = new HashMap<>();
//
//        Geolocation nearestGeolocation = nearestLocation(geolocationList, userLocation);
//        distance = distanceToCurrentLocation(userLocation.getLatitude(), userLocation.getLongitude(), nearestGeolocation.getLatitude(), nearestGeolocation.getLongitude());
//
//        resultList.put("Geolocation", nearestGeolocation);
//        resultList.put("Distance", distance);
//
//        return resultList;


//    public static Geolocation nearestLocation(Map<String, Location> locationMap, Location userLocation) {
//        Double userLong = userLocation.getLongitude();
//        Double userLat = userLocation.getLatitude();
//        Double min = 99999999.0;
//        String choosenKey = "";
//        Location choosenLocation = null;
//        for (Map.Entry<String, Location> entry : locationMap.entrySet()) {
//
//            Double longMap = entry.getValue().getLongitude();
//            Double latMap = entry.getValue().getLatitude();
//            Double temp = distanceToCurrentLocation(userLat, userLong, latMap, longMap);
//            if (temp < min) {
//                min = temp;
//                choosenKey = entry.getKey();
//                choosenLocation = entry.getValue();
//            }
//        }
//        Geolocation result = new Geolocation();
//        result.setFullName(choosenKey);
//        result.setLatitude(choosenLocation.getLatitude());
//        result.setLongitude(choosenLocation.getLongitude());
//        return result;
//
//    }

//TODO: замена Map<String,Location> на List<Geolocation> откуда брать distance?

