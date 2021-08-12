package ru.kumkuat.application.GameModule.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;

@Service
public class UpdateValidationService {
    @Autowired
    private UserService userService;

    public boolean validateUserForSimpleListener(Message updateMessage) {
        return userService.IsUserExist(updateMessage.getFrom().getId().longValue())&&
                !userService.getUserByTelegramId(updateMessage.getFrom().getId().longValue()).isAdmin();
    }
}
