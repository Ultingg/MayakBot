package ru.kumkuat.application.GameModule.Service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.kumkuat.application.GameModule.Bot.MarshakBot;
import ru.kumkuat.application.GameModule.Models.TelegramChat;

import java.util.Date;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
//@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class TimerService extends TimerTask {

    private static long DAY_OF_CHATTING = 3L;
    private IUseTimer iUseTimer;
    private final TelegramChatService telegramChatService;
    private final MarshakBot marshakBot;

    public TimerService(TelegramChatService telegramChatService, MarshakBot marshakBot) {
        this.telegramChatService = telegramChatService;
        this.marshakBot = marshakBot;
    }

    @Override
    public void run() {
        try {
            iUseTimer.TimerOperation();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Scheduled(cron = "0/10 * * * * *")
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
