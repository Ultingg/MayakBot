package ru.kumkuat.application.GameModule.Repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.kumkuat.application.GameModule.Models.PromocodeLog;

@Repository
public interface PromocodeLoggingRepository extends CrudRepository<PromocodeLog, Long> {

}
