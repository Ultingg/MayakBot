package ru.kumkuat.application.Repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.kumkuat.application.Geolocation.Geolocation;
@Repository
public interface GeolocationRepository extends CrudRepository<Geolocation,Long> {


    Geolocation getById(Long id);


}
