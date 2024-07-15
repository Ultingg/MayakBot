package ru.kumkuat.application.gameModule.executor;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.kumkuat.application.gameModule.bot.BotsSender;

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class ResponseProcessorTest {


    @Autowired
    private ResponseProcessor responseProcessor;
    @MockBean
    private ScheduledExecutorService executorService;

    @Test
    void testCollection() {

        List<BotsSender> botCollection = responseProcessor.getBotCollection();
        List<String> botNames = List.of("Harms", "Brodskiy", "Akhmatova", "Mayakovsky", "Marshak");
        assertTrue(botCollection.stream().map(BotsSender::getSecretName).collect(Collectors.toList()).containsAll(botNames));
        assertFalse(botCollection.isEmpty());
    }
}