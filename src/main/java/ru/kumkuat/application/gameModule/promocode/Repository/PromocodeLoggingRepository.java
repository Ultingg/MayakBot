package ru.kumkuat.application.gameModule.promocode.Repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.kumkuat.application.gameModule.promocode.Model.PromocodeLog;

@Repository
public interface PromocodeLoggingRepository extends CrudRepository<PromocodeLog, Long> {

}
