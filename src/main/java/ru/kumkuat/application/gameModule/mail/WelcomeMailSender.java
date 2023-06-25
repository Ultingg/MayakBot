package ru.kumkuat.application.gameModule.mail;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

@Slf4j
@Service
public class WelcomeMailSender {

    private Configuration configuration;

    public WelcomeMailSender() {
        configuration = new Configuration(Configuration.VERSION_2_3_28);
        configuration.setDefaultEncoding("UTF-8");
        configuration.setClassForTemplateLoading(MailService.class,"/templates/");
    }


    public String getHtmlMessage(String templateName, Map properties) {
        StringWriter out = new StringWriter();
        try {
            Template template = configuration.getTemplate(templateName, "UTF-8");
            template.process(properties, out);
        } catch (IOException | TemplateException e) {
           log.warn("Email wasn't sent templateName : {}", templateName);
        }
        return out.toString();
    }
}
