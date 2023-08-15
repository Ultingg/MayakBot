package ru.kumkuat.application.gameModule.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.kumkuat.application.gameModule.bot.MarshakBot;
import ru.kumkuat.application.gameModule.models.TelegramChat;

import java.util.Date;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class TimerService extends TimerTask {

    private final static String CRON ="0 0 1 * * *";
    private final static long DAY_OF_CHATTING = 3L;

    private IUseTimer iUseTimer;
    private final TelegramChatService telegramChatService;
    private final MarshakBot marshakBot;

    public TimerService(TelegramChatService telegramChatService, MarshakBot marshakBot) {
        this.telegramChatService = telegramChatService;
        this.marshakBot = marshakBot;
        log.info("======================CRON -{}, period of play {}", CRON, DAY_OF_CHATTING);
    }

    @Override
    public void run() {
        try {
            iUseTimer.TimerOperation();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Scheduled(cron = CRON, zone = "Europe/Moscow")
    public void checkAndKickUserFromChat() {
        int counter = 0;
        log.info("Time service start checking chats for users to kick out...");
        Date currentDate = new Date();
        List<TelegramChat> busyChats = telegramChatService.getBusyChats();
        log.info("{} busy chats was founded by service", busyChats.size());
        for (TelegramChat chat : busyChats) {
            if (checkDifferenceInDays(currentDate, chat.getStartPlayTime())) {
                Long userId = chat.getUserId();
                if (userId != null) {
                    marshakBot.getKickAllCommand().KickChatMember(marshakBot, userId);
                    counter++;
                }
            }
        }
        if(counter > 0) marshakBot.sendTimerOperationMessage(counter);
    }

    private boolean checkDifferenceInDays(Date currentDate, Date chatStartedDate) {
        long differenceInMillis = Math.abs(currentDate.getTime() - chatStartedDate.getTime());
        long daysDifference = TimeUnit.DAYS.convert(differenceInMillis, TimeUnit.MILLISECONDS);
        return daysDifference >= DAY_OF_CHATTING;
    }

    public void setTimerOperation(IUseTimer iUseTimer) {
        this.iUseTimer = iUseTimer;
    }
}
