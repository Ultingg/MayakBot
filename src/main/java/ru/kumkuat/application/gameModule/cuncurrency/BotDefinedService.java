package ru.kumkuat.application.gameModule.cuncurrency;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.kumkuat.application.gameModule.bot.BotsSender;
import ru.kumkuat.application.gameModule.collections.ResponseContainer;
import ru.kumkuat.application.gameModule.cuncurrency.executor.SimpleUpdateExecutor;

import java.util.List;

@Slf4j
@Service
public class BotDefinedService {
    @Autowired
    private List<BotsSender> botCollection;
    private final SimpleUpdateExecutor executor;

    public BotDefinedService(SimpleUpdateExecutor executor) {
        this.executor = executor;
    }

    public void arrangeResponse(ResponseContainer container) {
        String botName = container.getBotName();
        var botSender = botCollection.stream().filter(bot -> bot.getSecretName().equals(botName)).findFirst().get();
        try {
            executor.execute(container, botSender);
        } catch (InterruptedException e) {
            log.error(e.getMessage());
        }
    }

}
