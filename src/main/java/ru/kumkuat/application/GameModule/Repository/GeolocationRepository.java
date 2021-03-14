package ru.kumkuat.application.GameModule.Repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.kumkuat.application.GameModule.Models.Geolocation;

@Repository
public interface GeolocationRepository extends CrudRepository<Geolocation, Long> {
    Geolocation getById(Long id);
}
