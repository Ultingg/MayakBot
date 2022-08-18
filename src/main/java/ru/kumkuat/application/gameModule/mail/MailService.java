package ru.kumkuat.application.gameModule.mail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class MailService {


    @Autowired
    private WelcomeMailSender welcomeMailSender;

    public String sendWelcomeMail() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("","");



                return welcomeMailSender.getHtmlMessage("welcomeCode2.ftl", properties);
    }

}
