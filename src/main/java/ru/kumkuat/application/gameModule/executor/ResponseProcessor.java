package ru.kumkuat.application.gameModule.executor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.kumkuat.application.gameModule.bot.BotsSender;
import ru.kumkuat.application.gameModule.collections.ResponseContainer;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class ResponseProcessor {

    private final ScheduledExecutorService executorService;
    @Autowired
    private final List<BotsSender> botCollection;

    public ResponseProcessor(List<BotsSender> botCollection) {
        this.botCollection = botCollection;
        this.executorService = Executors.newScheduledThreadPool(50);
    }

    public List<BotsSender> getBotCollection() {
        return botCollection;
    }

    public void processTasks(List<TaskContainer> containers){
        for(TaskContainer task: containers) {
            long delay = task.getDelay();
            BotsSender botsSender = getBotByName(task.getBotName());

            executorService.schedule(task, delay, TimeUnit.MILLISECONDS);
        }
    }

    public void processTasks(List<ResponseContainer> containers){
        for(ResponseContainer task: containers) {
            long delay = task.getTimingOfReply();
            BotsSender botsSender = getBotByName(task.getBotName());

//            executorService.schedule(task, delay, TimeUnit.MILLISECONDS);
        }
    }


    private BotsSender getBotByName(String botName){
        return botCollection.stream().filter(bot -> bot.getSecretName().equals(botName)).findFirst().orElseThrow();
    }
}
