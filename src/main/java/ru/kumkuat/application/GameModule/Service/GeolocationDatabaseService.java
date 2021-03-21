package ru.kumkuat.application.GameModule.Service;

import org.springframework.stereotype.Service;
import org.w3c.dom.Node;
import ru.kumkuat.application.GameModule.Models.Geolocation;
import ru.kumkuat.application.GameModule.Repository.GeolocationRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class GeolocationDatabaseService {

    private final GeolocationRepository geolocationRepository;

    public GeolocationDatabaseService(GeolocationRepository geolocationRepository) {
        this.geolocationRepository = geolocationRepository;
        cleanAll();
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

    public long setGeolocationIntoDB(Node triggerNode) {
        Geolocation geolocation = new Geolocation();
        var nodes = triggerNode.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            if (nodes.item(i).getNodeName().equals("fullName")) {
                geolocation.setFullName(nodes.item(i).getFirstChild().getNodeValue());
            } else if (nodes.item(i).getNodeName().equals("longitude")) {
                geolocation.setLongitude(Double.parseDouble(nodes.item(i).getFirstChild().getNodeValue()));
            } else if (nodes.item(i).getNodeName().equals("latitude")) {
                geolocation.setLatitude(Double.parseDouble(nodes.item(i).getFirstChild().getNodeValue()));
            }
        }
        geolocationRepository.save(geolocation);
        return geolocation.getId();
    }

    public void cleanAll(){
        geolocationRepository.deleteAll();
    }
}



