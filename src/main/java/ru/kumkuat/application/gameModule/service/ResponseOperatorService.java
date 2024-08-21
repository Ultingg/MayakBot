package ru.kumkuat.application.gameModule.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.kumkuat.application.gameModule.collections.ResponseContainer;
import ru.kumkuat.application.gameModule.cuncurrency.UpdateProcessor;
import ru.kumkuat.application.gameModule.marshakCommands.CommandService;

import java.util.List;

@Slf4j
@Service
public class ResponseOperatorService {

    private final CommandService commandService;
    private final ResponseService responseService;
    private final UpdateProcessor updateProcessor;

    public ResponseOperatorService(CommandService commandService, ResponseService responseService, UpdateProcessor updateProcessor) {
        this.commandService = commandService;
        this.responseService = responseService;
        this.updateProcessor = updateProcessor;
    }

    public void responseTypeResolver(Message incomingMessage) {
        List<ResponseContainer> responseContainers = responseService.messageReceiver(incomingMessage);
        responseResolver(responseContainers);
    }

    private void responseResolver(List<ResponseContainer> responseContainers) {
        if (!responseContainers.isEmpty()) {
            updateProcessor.process(responseContainers);
        }
    }
}

