package ru.kumkuat.application.GameModule.Promocode.Repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import ru.kumkuat.application.GameModule.Promocode.Model.TimePadOrder;

import java.util.List;

public interface TimPadOrderRepository extends CrudRepository<TimePadOrder, Long> {


    TimePadOrder getById(Long id);

    TimePadOrder getByOrOrderNumber(Long orderNumber);

    TimePadOrder getByEmail(String email);

    boolean existsByOrderNumber(Long orderNumber);

    @Query(value = "SELECT * FROM tporder  where is_notified = 0",
            nativeQuery = true)
    List<TimePadOrder> getAllNotNotifiedOrders();
}
