package ru.kumkuat.application.gameModule.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.kumkuat.application.gameModule.api.services.EmailService;
import ru.kumkuat.application.gameModule.api.services.XMLParseService;

@RestController
@RequestMapping("/api")
public class ApiController {


    private final XMLParseService xmlParseService;
    private final EmailService emailService;


    public ApiController(XMLParseService xmlParseService, EmailService emailService) {
        this.xmlParseService = xmlParseService;
        this.emailService = emailService;
    }


    @GetMapping("/parsexml")
    public ResponseEntity<String> parseTimePAdOrder() {
        return ResponseEntity.ok(xmlParseService.parseXmlFile());
    }

    @PostMapping("/send_mail")
    public ResponseEntity<String> sendMailTimePadCustomers(){



        return ResponseEntity.ok(emailService.sendMail());
    }
}
