package ru.kumkuat.application.GameModule.Promocode.Repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.kumkuat.application.GameModule.Promocode.Model.DisposablePromocode;

import java.util.List;

@Repository
public interface DisposablePromocodeRepository extends CrudRepository<DisposablePromocode, Long> {


    DisposablePromocode getByValue(String value);

    @Query(value = "select pc from DisposablePromocode pc where pc.isUsed = false and pc.isSent = false")
    List<DisposablePromocode> getDisposablePromocodesNotSent();
}


