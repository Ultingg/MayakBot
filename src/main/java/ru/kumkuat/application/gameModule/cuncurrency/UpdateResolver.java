package ru.kumkuat.application.gameModule.cuncurrency;

import ru.kumkuat.application.gameModule.collections.ResponseContainer;
import ru.kumkuat.application.gameModule.cuncurrency.executor.SimpleUpdateExecutor;

/**
 * Class wrapper for concurrency process.
 */
public class UpdateResolver implements Runnable {
    private final ResponseContainer updateContainer;
    private final SimpleUpdateExecutor simpleUpdateExecutor;

    public UpdateResolver(ResponseContainer updateContainer, SimpleUpdateExecutor simpleUpdateExecutor) {
        this.updateContainer = updateContainer;
        this.simpleUpdateExecutor = simpleUpdateExecutor;
    }

    @Override
    public void run() {
        simpleUpdateExecutor.execute(updateContainer);
    }
}
