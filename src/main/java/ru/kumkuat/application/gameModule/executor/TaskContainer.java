package ru.kumkuat.application.gameModule.executor;

import ru.kumkuat.application.gameModule.bot.BotsSender;
import ru.kumkuat.application.gameModule.collections.ResponseContainer;

public class TaskContainer implements Runnable {

    private Long delay;
    private String botName;

    @Override
    public void run() {

    }

    private void sendResponseToUser(ResponseContainer responseContainer, BotsSender botsSender) {

        if (responseContainer.hasGeolocation()) {
            botsSender.sendLocation(responseContainer.getSendLocation());
        }
        if (responseContainer.hasAudio()) {
            botsSender.sendVoice(responseContainer.getSendVoice());
        }
        if (responseContainer.hasPicture()) {
            botsSender.sendPicture(responseContainer.getSendPhoto());
        }
        if (responseContainer.hasText()) {
            botsSender.sendMessage(responseContainer.getSendMessage());
        }
        if (responseContainer.hasSticker()) {
            botsSender.sendSticker(responseContainer.getSendSticker());
        }
    }


    public String getBotName() {
        return botName;
    }

    public void setBotName(String botName) {
        this.botName = botName;
    }

    public Long getDelay() {
        return delay;
    }

    public void setDelay(Long delay) {
        this.delay = delay;
    }
}
