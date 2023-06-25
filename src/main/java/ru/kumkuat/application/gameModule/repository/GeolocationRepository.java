package ru.kumkuat.application.gameModule.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.kumkuat.application.gameModule.models.Geolocation;

@Repository
public interface GeolocationRepository extends CrudRepository<Geolocation, Long> {
    Geolocation getById(Long id);
}
