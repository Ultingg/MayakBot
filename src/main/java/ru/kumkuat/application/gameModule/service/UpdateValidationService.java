package ru.kumkuat.application.gameModule.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.User;

@Service
@Slf4j
public class UpdateValidationService {
    @Autowired
    private UserService userService;

    public void registerUser(User user) {
        if (!userService.IsUserExist(user.getId().longValue())) {
            try {
                log.info("User id: {} added to DB", user.getId());
                userService.setUserIntoDB(user);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
