package ru.kumkuat.application.GameModule.Service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.kumkuat.application.GameModule.Models.DisposablePromocode;
import ru.kumkuat.application.GameModule.Repository.DisposablePromocodeRepository;

import java.time.LocalDateTime;

@Slf4j
@Service
public class PromocodeServiceImpl implements PromocodeService {


    private final DisposablePromocodeRepository promocodeRepository;
    private final PromocodeGenerator generator;

    public PromocodeServiceImpl(DisposablePromocodeRepository promocodeRepository, PromocodeGenerator generator) {
        this.promocodeRepository = promocodeRepository;
        this.generator = generator;
    }


    @Override
    public boolean checkPromocode(String value) {
        DisposablePromocode disposablePromocode = promocodeRepository.getByValue(value);
        if (disposablePromocode != null) {
            if (!disposablePromocode.isUsed()) {
                disposablePromocode.setUsed(true);
                disposablePromocode.setPromocodeUsed(LocalDateTime.now());
                try {
                    promocodeRepository.save(disposablePromocode);
                    log.info("Promocode value:{}, id:{} confirmed. It's status changed to \"used\".", disposablePromocode.getValue(), disposablePromocode.getId());
                } catch (Exception e) {
                    log.error("Some shit has happened with changing promocode value:{}!!!", value);
                    return false;
                }
                return true;
            } else {
                log.info("Promocode value:{}, id:{} is used.", disposablePromocode.getValue(), disposablePromocode.getId());
                return false;
            }
        } else {
            log.info("Promocode value:{} doesn't exist in database.", value);
            return false;
        }
    }

    @Override
    public DisposablePromocode createNewDisposalPormocode() {
        DisposablePromocode disposablePromocode = createNewDisposalPromocode();
        DisposablePromocode newDisposablePromocode =  promocodeRepository.save(disposablePromocode);
        log.info("Promocode with value {} was created.", newDisposablePromocode.getValue());
        return newDisposablePromocode;
    }


    private DisposablePromocode createNewDisposalPromocode() {
        String value = generator.generateValueForDisposalPromocode();
        DisposablePromocode disposablePromocode = new DisposablePromocode();
        disposablePromocode.setValue(value);
        disposablePromocode.setUsed(false);
        return disposablePromocode;
    }
}
