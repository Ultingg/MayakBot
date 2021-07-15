package ru.kumkuat.application.GameModule.Service;


import org.springframework.stereotype.Service;
import ru.kumkuat.application.GameModule.Models.BGUser;
import ru.kumkuat.application.GameModule.Repository.BGUserRepository;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class BGUserService {


    private final BGUserRepository bgUserRepository;
    private final String START_DATE = "2021-07-31T";

    public BGUserService(BGUserRepository bgUserRepository) {
        this.bgUserRepository = bgUserRepository;
    }

    public void setBGUserToDB(BGUser bgUser) {
        bgUserRepository.save(bgUser);
    }

    public BGUser getBGUserByUsername(String telegramUsername) {
        return bgUserRepository.findBGUserByTelegramUserName(telegramUsername);
    }

    public boolean isBGUserExistByUsername(String telegramUsername) {
        return (bgUserRepository.findBGUserByTelegramUserName(telegramUsername) != null);
    }

    public List<BGUser> getAll() {
        List<BGUser> bgUserList = new ArrayList<>();
        bgUserRepository.findAll().iterator().forEachRemaining(bgUserList::add);
        return bgUserList;
    }

// не знаю зачем нам endTime... если люди не влезут интервал будем их пихать куда влезут)
    public void calculateAndSetStartTimeForBGUser(BGUser bgUser) {
        String time = bgUser.getPreferredTime();

        String resolvedTimeString = time.replaceAll("[\\Wa-zA-Z]", "");
        LocalTime startTime = LocalTime.parse("10:00");
        LocalTime endTime = LocalTime.parse("11:00");
        int length = resolvedTimeString.length();
        if (length == 4) {
            startTime = LocalTime.parse(resolvedTimeString.substring(0, 2) + "00");
            endTime = LocalTime.parse(resolvedTimeString.substring(2) + "00");
        } else {
            startTime = LocalTime.parse(resolvedTimeString.substring(0, 4) + "00");
            endTime = LocalTime.parse(resolvedTimeString.substring(4) + "00");
        }
        if (bgUser.getStartWith() != null) {
            startTime = getTimeOfPreferredFriend(bgUser.getStartWith());
            startTime = insertInSchedule(startTime);
        } else {
            startTime = insertInSchedule(startTime);
        }
        bgUser.setStartTime(startTime);
        bgUserRepository.save(bgUser);
    }

    private LocalTime getTimeOfPreferredFriend(String username) {
        return bgUserRepository.findStartTimeByTelegramUserName(username);
    }

    public LocalTime insertInSchedule(LocalTime rawStart) {
        boolean chek = bgUserRepository.existsBGUserByStartTime(rawStart);
        while (chek) {
            rawStart.plusMinutes(2);
            chek = bgUserRepository.existsBGUserByStartTime(rawStart);
        }
        return rawStart;
    }


}
