package ru.kumkuat.application.gameModule.promocode.Service;

import ru.kumkuat.application.gameModule.promocode.Model.TimePadOrder;

import java.util.List;

/**
 * General interface to operate with Users
 */
public interface TimePadOrderService {


    TimePadOrder getOrderById(Long id);

    TimePadOrder save(TimePadOrder userToSave);

    TimePadOrder update(TimePadOrder userToUpdate);

    TimePadOrder getByOrderNumber(Long orderNumber);

    TimePadOrder getOrderByEmail(String email);

    boolean isOrderExistsByOrderNumber(Long orderNumber);

    List<TimePadOrder> getAllNotNotifiedOrders();

}
