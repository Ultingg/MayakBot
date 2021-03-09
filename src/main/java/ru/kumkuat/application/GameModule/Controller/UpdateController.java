package ru.kumkuat.application.GameModule.Controller;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
public class UpdateController {


    public void receiveUpdate(Update update){
        Message message = update.getMessage();

    }

}
