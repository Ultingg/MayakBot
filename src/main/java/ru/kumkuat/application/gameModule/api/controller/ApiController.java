package ru.kumkuat.application.gameModule.api.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.kumkuat.application.gameModule.api.services.ChatDetector;
import ru.kumkuat.application.gameModule.api.services.EmailService;
import ru.kumkuat.application.gameModule.api.services.XMLParseService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api")
public class ApiController {


    private final XMLParseService xmlParseService;
    private final EmailService emailService;
    private final ChatDetector detector;


    public ApiController(XMLParseService xmlParseService, EmailService emailService, ChatDetector detector) {
        this.xmlParseService = xmlParseService;
        this.emailService = emailService;
        this.detector = detector;
    }


    @GetMapping("/parsexml")
    public ResponseEntity<String> parseTimePAdOrder() {
        log.info("API request /parsexml");
        return ResponseEntity.ok(xmlParseService.parseXmlFile());
    }

    @PostMapping("/send-mail")
    public ResponseEntity<List<String>> sendMailTimePadCustomers() {
        log.info("API request /send_email");
        return ResponseEntity.ok(emailService.sendMail());
    }

    @PutMapping("/detect-chat")
    public ResponseEntity<String> detectChatById(@RequestParam Long chatId) {
        log.info("API request to detect chat");
        detector.detectChat(chatId);
        log.info("Chat " + chatId + " detected.");
        return ResponseEntity.ok("Chat detected");
    }
}
