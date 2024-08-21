package ru.kumkuat.application.gameModule.cuncurrency;

import org.springframework.stereotype.Service;
import ru.kumkuat.application.gameModule.collections.ResponseContainer;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class UpdateProcessor {
    private final ScheduledExecutorService executorService;
    private final BotDefinedService botDefinedService;

    public UpdateProcessor(BotDefinedService botDefinedService) {
        this.executorService = Executors.newScheduledThreadPool(8);
        this.botDefinedService = botDefinedService;
    }

    public void process(List<ResponseContainer> tasks) {
        for (ResponseContainer task : tasks) {
            int delay = task.getTimingOfReply();
            UpdateResolver resolver = new UpdateResolver(task, botDefinedService);
            executorService.schedule(resolver, delay, TimeUnit.SECONDS);
        }
    }
}
