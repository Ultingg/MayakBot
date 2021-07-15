package ru.kumkuat.application.GameModule.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.mail.internet.MimeMessage;
import java.util.Date;

@Controller
public class MailController {

    private final String mailAdress = "solnce-3@yandex.ru";

    @Autowired
    private JavaMailSender emailSender;

    @ResponseBody
    @RequestMapping("/sendEmail")
    public void sendSimpleEmail(String mailRecipient, String subject, String text) {
        MimeMessagePreparator preparator = new MimeMessagePreparator() {
            public void prepare(MimeMessage mimeMessage) {
                MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage);
                try {
                    messageHelper.setTo(mailRecipient);
                    messageHelper.setFrom(mailAdress);
                    messageHelper.setSubject(subject);
                    messageHelper.setSentDate(new Date());
                    messageHelper.setText(text, true);
                } catch (Exception ex) {

                }
            }
        };
        this.emailSender.send(preparator);
    }
}
