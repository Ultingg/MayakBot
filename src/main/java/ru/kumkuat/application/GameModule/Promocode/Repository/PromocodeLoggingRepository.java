package ru.kumkuat.application.GameModule.Promocode.Repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.kumkuat.application.GameModule.Promocode.Model.PromocodeLog;

@Repository
public interface PromocodeLoggingRepository extends CrudRepository<PromocodeLog, Long> {

}
