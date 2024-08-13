package ru.kumkuat.application.gameModule.promocode.Service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.kumkuat.application.gameModule.promocode.Model.TimePadOrder;
import ru.kumkuat.application.gameModule.promocode.Repository.TimPadOrderRepository;

import java.util.List;


@Slf4j
@Service
public class TimePadOrderServiceImpl implements TimePadOrderService {

    private final TimPadOrderRepository repository;

    public TimePadOrderServiceImpl(TimPadOrderRepository repository) {
        this.repository = repository;
    }


    @Override
    public TimePadOrder getOrderById(Long id) {
       return repository.getById(id);
    }

    @Override
    public TimePadOrder save(TimePadOrder userToSave) {
        return repository.save(userToSave);
    }

    @Override
    public TimePadOrder update(TimePadOrder userToUpdate) {
        return repository.save(userToUpdate);
    }

    @Override
    public TimePadOrder getByOrderNumber(Long orderNumber) {
        return repository.getByOrOrderNumber(orderNumber);
    }

    @Override
    public TimePadOrder getOrderByEmail(String email) {
        return repository.getByEmail(email);
    }

    public boolean isOrderExistsByOrderNumber(Long orderNumber) {
        return repository.existsByOrderNumber(orderNumber);
    }

    @Override
    public List<TimePadOrder> getAllNotNotifiedOrders() {
        return repository.getAllByIsNotifiedFalse();
    }
}
