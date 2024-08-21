package ru.kumkuat.application.gameModule.cuncurrency;

import ru.kumkuat.application.gameModule.collections.ResponseContainer;

public class UpdateResolver implements Runnable {
    private final ResponseContainer updateContainer;
    private final BotDefinedService botDefinedService;

    public UpdateResolver(ResponseContainer updateContainer, BotDefinedService botDefinedService) {
        this.botDefinedService = botDefinedService;
        this.updateContainer = updateContainer;
    }

    @Override
    public void run() {
        botDefinedService.arrangeResponse(updateContainer);
    }
}
