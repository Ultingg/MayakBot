package ru.kumkuat.application.gameModule.cuncurrency;

import org.springframework.stereotype.Service;
import ru.kumkuat.application.gameModule.collections.ResponseContainer;
import ru.kumkuat.application.gameModule.cuncurrency.executor.SimpleUpdateExecutor;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Service that distribute responseContainers {@link ResponseContainer} by wrapper class {@link UpdateResolver}.
 */
@Service
public class UpdateProcessor {
    private final ScheduledExecutorService executorService;
    private final SimpleUpdateExecutor simpleUpdateExecutor;

    public UpdateProcessor(SimpleUpdateExecutor simpleUpdateExecutor) {
        this.simpleUpdateExecutor = simpleUpdateExecutor;
        this.executorService = Executors.newScheduledThreadPool(8);
    }

    public void process(List<ResponseContainer> tasks) {
        for (ResponseContainer task : tasks) {
            int delay = task.getTimingOfReply();
            UpdateResolver resolver = new UpdateResolver(task,  simpleUpdateExecutor);
            executorService.schedule(resolver, delay, TimeUnit.SECONDS);
        }
    }
}
