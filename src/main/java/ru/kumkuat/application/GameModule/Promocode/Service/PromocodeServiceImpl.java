package ru.kumkuat.application.GameModule.Promocode.Service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.kumkuat.application.GameModule.Promocode.Model.DisposablePromocode;
import ru.kumkuat.application.GameModule.Promocode.Repository.DisposablePromocodeRepository;

import java.time.LocalDateTime;
import java.util.List;

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
                try {
                    markAsUsedPromocode(disposablePromocode);
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
        DisposablePromocode newDisposablePromocode = promocodeRepository.save(disposablePromocode);
        log.info("Promocode with value {} was created.", newDisposablePromocode.getValue());
        return newDisposablePromocode;
    }

    @Override
    public DisposablePromocode getDisposalPromocode() {
        List<DisposablePromocode> listOfNotSentPromocodes = promocodeRepository.getDisposablePromocodesNotSent();
        if (!listOfNotSentPromocodes.isEmpty()) {
            DisposablePromocode firstPromocode = listOfNotSentPromocodes.get(0);
            try{
                markAsSentPromocode(firstPromocode);
            }  catch (Exception e) {
            log.error("Some shit has happened with changing promocode value:{}!!!", firstPromocode.getValue());
        }
            log.info("Promocode {} was provided", firstPromocode.getValue());
            return firstPromocode;
        } else {
            DisposablePromocode newPromocode = createNewDisposalPormocode();
            try{
                markAsSentPromocode(newPromocode);
            }  catch (Exception e) {
                log.error("Some shit has happened with changing promocode value:{}!!!", newPromocode.getValue());
            }
            log.info("Promocode {} was provided", newPromocode.getValue());
            return newPromocode;
        }
    }

    public DisposablePromocode getNewDisposalMarkedPromocode() {
        DisposablePromocode markedPromocode = createNewDisposalPormocode();
        markAsSentPromocode(markedPromocode);
        return markedPromocode;
    }

    private void markAsUsedPromocode(DisposablePromocode promocode) {
        promocode.setUsed(true);
        promocode.setPromocodeUsed(LocalDateTime.now());
        promocodeRepository.save(promocode);
    }

    private void markAsSentPromocode(DisposablePromocode promocode) {
        promocode.setSent(true);
        promocodeRepository.save(promocode);
    }

    private DisposablePromocode createNewDisposalPromocode() {
        String value = generator.generateValueForDisposalPromocode();
        DisposablePromocode disposablePromocode = new DisposablePromocode();
        disposablePromocode.setValue(value);
        disposablePromocode.setUsed(false);
        disposablePromocode.setSent(false);
        return disposablePromocode;
    }
}
