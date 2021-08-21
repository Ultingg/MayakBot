package ru.kumkuat.application.GameModule.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;

@Service
public class UpdateValidationService {
    @Autowired
    private UserService userService;

    public void registerUser(User user) {
        if (!userService.IsUserExist(user.getId().longValue())) {
            try {
                userService.setUserIntoDB(user);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
