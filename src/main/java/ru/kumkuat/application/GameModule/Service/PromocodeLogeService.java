package ru.kumkuat.application.GameModule.Service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.kumkuat.application.GameModule.Models.PromocodeLog;
import ru.kumkuat.application.GameModule.Repository.PromocodeRepository;

import java.time.LocalDateTime;

@Slf4j
@Service
@PropertySource(value = "file:../resources/promocode.yml")
public class PromocodeLogeService {

    @Value("${promocode}")
    private String promocode;
    @Value("${tsystemscode}")
    private String tsystemscode;

    private final UserService userService;
    private final PromocodeRepository promocodeRepository;

    public PromocodeLogeService(UserService userService, PromocodeRepository promocodeRepository) {
        this.userService = userService;
        this.promocodeRepository = promocodeRepository;
    }

    public PromocodeLog save(PromocodeLog promocodeLog) {
        return promocodeRepository.save(promocodeLog);
    }

    public String getTsystemscode()
    {
        return tsystemscode;
    }

    public String getPromocode() {
        return promocode;
    }

    public void pomocodeLogging(User user) {
        Long userId = user.getId().longValue();
        boolean isPromo = userService.isUserPromoByTelegramId(userId);
        if (isPromo) {
            log.info("User id:{} use promocode: {}", userId, getPromocode());
            PromocodeLog promocodeLog = new PromocodeLog();
            promocodeLog.setPromocodeText(getPromocode());
            promocodeLog.setPromocodeUsed(LocalDateTime.now());
            promocodeLog.setUserId(userId);
            save(promocodeLog);
            userService.setUserPromoFlag(userId, false);
            log.info("User id:{}  promocode was turned off", userId);
        }
        log.info("User id:{} hasn't use promocode", userId);
    }
}
