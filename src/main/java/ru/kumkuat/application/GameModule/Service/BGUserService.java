package ru.kumkuat.application.GameModule.Service;


import org.springframework.stereotype.Service;
import ru.kumkuat.application.GameModule.Models.BGUser;
import ru.kumkuat.application.GameModule.Repository.BGUserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class BGUserService {


    private final BGUserRepository bgUserRepository;


    public BGUserService(BGUserRepository bgUserRepository) {
        this.bgUserRepository = bgUserRepository;
    }

    public void setBGUserToDB(BGUser bgUser) { bgUserRepository.save(bgUser);
    }

    public BGUser getBGUserByUsername(String telegramUsername) {
        return bgUserRepository.findBGUserByTelegramUserName(telegramUsername);
    }

    public boolean isBGUserExistByUsername(String telegramUsername) {
        return (bgUserRepository.findBGUserByTelegramUserName(telegramUsername) != null);
    }

    public List<BGUser> getAll(){
        List<BGUser> bgUserList = new ArrayList<>();
        bgUserRepository.findAll().iterator().forEachRemaining(bgUserList:: add);
        return bgUserList;
    }

    public void calculateStartTimeForBGUser(BGUser bgUser) {
        String t = bgUser.getPreferredTime();
        String[] interval = bgUser.getPreferredTime().split("-");
        Double startOfInterval = Double.valueOf(interval[0]);
        Double endOfInterval = Double.valueOf(interval[1]);
        Double startTime = startOfInterval + 0.2;

    }



}
