package ru.kumkuat.application.Service;

import org.springframework.stereotype.Service;
import ru.kumkuat.application.Geolocation.Geolocation;
import ru.kumkuat.application.Repository.GeolocationRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class GeolocationService {

    private final GeolocationRepository geolocationRepository;

    public GeolocationService(GeolocationRepository geolocationRepository) {
        this.geolocationRepository = geolocationRepository;
    }

    public Geolocation getGeolocationById(Long id) {
       return geolocationRepository.getById(id);
    }

    public List<Geolocation> getAll() {
            List<Geolocation> geolocationList = new ArrayList<>();
            Iterable<Geolocation> repoCollection = geolocationRepository.findAll();
            for (Geolocation geo : repoCollection) geolocationList.add(geo);

            return geolocationList;
        }
    }



