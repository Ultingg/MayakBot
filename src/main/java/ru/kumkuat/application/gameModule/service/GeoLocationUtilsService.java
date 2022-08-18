package ru.kumkuat.application.gameModule.service;

import org.springframework.stereotype.Service;

import static java.lang.Math.*;

@Service
public class GeoLocationUtilsService {
    public GeoLocationUtilsService() {
    }

    public static double distanceToCurrentLocation(Double userLati, Double userLong, Double aimLati, Double aimLong) {
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
}


