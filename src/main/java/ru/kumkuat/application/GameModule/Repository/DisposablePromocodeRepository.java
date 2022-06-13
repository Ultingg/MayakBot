package ru.kumkuat.application.GameModule.Repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.kumkuat.application.GameModule.Models.DisposablePromocode;

@Repository
public interface DisposablePromocodeRepository extends CrudRepository<DisposablePromocode, Long> {


    DisposablePromocode getByValue(String value);
}


