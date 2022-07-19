package ru.kumkuat.application.GameModule.Promocode.Service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.kumkuat.application.GameModule.Promocode.Model.TimePadOrder;
import ru.kumkuat.application.GameModule.Promocode.Repository.TimPadOrderRepository;

import java.util.List;


@Slf4j
@Service
public class TimPadOrderServiceImpl implements TimPadOrderService {

    private final TimPadOrderRepository repository;

    public TimPadOrderServiceImpl(TimPadOrderRepository repository) {
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
        return repository.getAllNotNotifiedOrders();
    }
}
