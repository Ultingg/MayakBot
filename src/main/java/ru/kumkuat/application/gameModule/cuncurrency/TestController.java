package ru.kumkuat.application.gameModule.cuncurrency;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.kumkuat.application.gameModule.cuncurrency.executor.TestDto;

@RestController
@RequestMapping("/cunc")
public class TestController {
    private final UpdateProcessor updateProcessor;

    public TestController(UpdateProcessor updateProcessor) {
        this.updateProcessor = updateProcessor;
    }

    @PostMapping
    public String startTest(@RequestBody TestDto dto) {

        updateProcessor.process(dto.getUpdates());
        return "DONE";
    }
}
