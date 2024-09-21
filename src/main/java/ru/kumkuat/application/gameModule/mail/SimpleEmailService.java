package ru.kumkuat.application.gameModule.mail;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Controller;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import ru.kumkuat.application.gameModule.promocode.Model.TimePadOrder;
import ru.kumkuat.application.gameModule.promocode.Service.PromocodeService;
import ru.kumkuat.application.gameModule.promocode.Service.TimePadOrderService;

import java.util.Date;
import java.util.List;

@Slf4j
@Controller
@PropertySource(value = "file:../resources/externalsecret.yml")
public class SimpleEmailService {
    private final static Logger logger = LoggerFactory.getLogger(SimpleEmailService.class);
    @Value("${email.address}")
    private String EMAIL;
    private final TemplateEngine templateEngine;
    private final TimePadOrderService timePadOrderService;
    private final PromocodeService promocodeService;
    private final JavaMailSender emailSender;

    public SimpleEmailService(TemplateEngine templateEngine,
                              TimePadOrderService timePadOrderService,
                              PromocodeService promocodeService,
                              JavaMailSender emailSender) {
        this.templateEngine = templateEngine;
        this.timePadOrderService = timePadOrderService;
        this.promocodeService = promocodeService;
        this.emailSender = emailSender;
    }


    /**
     * Method get all users from tpOrder table that are not notified and send them letter.
     * After that it marks them as notified.
     */
    public int processMailSending() {
        logger.info("Email sending start");
        List<TimePadOrder> orders = timePadOrderService.getAllNotNotifiedOrders();
        int emailToSend = orders.size();
        logger.info("Emails to send: " + emailToSend);
        for (var timePadOrder : orders) {
            int amountOfLetters = timePadOrder.getAmountTickets();
            for (int i = 0; i < amountOfLetters; i++) {
                Context context = new Context();
                context.setVariable("user", timePadOrder.getFirstName() + " " + timePadOrder.getLastName());
                context.setVariable("promocode", promocodeService.getDisposalPromocode().getValue());
                String text = templateEngine.process("Emails/WelcomeCode.html", context);
                sendSimpleEmail(timePadOrder.getEmail(), "Важная информация для старта спектакля «Проспект Поэтов»", text);
            }
            timePadOrder.setIsNotified(true);
            timePadOrderService.save(timePadOrder);
        }

        logger.info("Email sending finished");
        return emailToSend;
    }

    /**
     * Method get all users from tpOrder table that are not notified and send them letter.
     * After that it marks them as notified.
     */
    public int processMailSendingWithTime() {
        logger.info("Email with time sending start");
        List<TimePadOrder> orders = timePadOrderService.getAllNotNotifiedOrders();
        int emailToSend = orders.stream().reduce(0, (tickets, order) -> tickets + order.getAmountTickets(), Integer::sum);
        logger.info("Emails to send: " + emailToSend);
        for (var timePadOrder : orders) {
            int amountOfLetters = timePadOrder.getAmountTickets();
            for (int i = 0; i < amountOfLetters; i++) {
                Context context = new Context();
                context.setVariable("time", timePadOrder.getTime());
                context.setVariable("promocode", promocodeService.getDisposalPromocode().getValue());
                String text = templateEngine.process("Emails/WelcomeCode2.html", context);
                sendSimpleEmail(timePadOrder.getEmail(), "Важная информация для старта спектакля «Проспект Поэтов»", text);
            }
            timePadOrder.setIsNotified(true);
            timePadOrderService.save(timePadOrder);
        }

        logger.info("Email with time  sending finished");
        return emailToSend;
    }


    public int sendPromoMail() {
        logger.info("Email with time sending start");
        List<TimePadOrder> orders = timePadOrderService.getAllNotNotifiedOrders();
        int emailToSend = orders.size();
        String text = templateEngine.process("Emails/promoMail.html", new Context());
        logger.info("Emails to send: " + emailToSend);
        for (var timePadOrder : orders) {

            sendSimpleEmail(timePadOrder.getEmail(), "Анонс следующего спектакля «Проспект Поэтов»", text);
            timePadOrder.setIsNotified(true);
            timePadOrderService.save(timePadOrder);
        }

        logger.info("Email with time  sending finished");
        return emailToSend;
    }

    public void sendSimpleEmail(String mailRecipient, String subject, String text) {
        MimeMessagePreparator preparator = mimeMessage -> {
            System.setProperty("mail.mime.splitlongparameters", "false");
            try {
                MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
                messageHelper.setTo(mailRecipient);
                messageHelper.setFrom(EMAIL);
                messageHelper.setSubject(subject);
                messageHelper.setSentDate(new Date());
                messageHelper.setText(text, true);
                log.info("Email was sent to " + mailRecipient);
            } catch (Exception ex) {
                log.error(ex.getMessage());
            }
        };
        this.emailSender.send(preparator);
    }
}
