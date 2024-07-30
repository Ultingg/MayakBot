package ru.kumkuat.application.gameModule.api.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.kumkuat.application.gameModule.api.services.ChatDetector;
import ru.kumkuat.application.gameModule.api.services.XMLParseService;
import ru.kumkuat.application.gameModule.mail.SimpleEmailService;
import ru.kumkuat.application.gameModule.utils.CommonResponse;

@Slf4j
@RestController
@RequestMapping("/api")
public class ApiController {

    private final SimpleEmailService mailService;
    private final XMLParseService xmlParseService;
    private final ChatDetector detector;

    public ApiController(SimpleEmailService mailService, XMLParseService xmlParseService, ChatDetector detector) {
        this.mailService = mailService;
        this.xmlParseService = xmlParseService;
        this.detector = detector;
    }

    @GetMapping("/parsexml")
    public ResponseEntity<String> parseTimePAdOrder() {
        log.info("API request parsexml");
        return ResponseEntity.ok(xmlParseService.parseXmlFile());
    }

    @PostMapping("/send-email")
    public ResponseEntity<CommonResponse> sendEmails() {
        log.info("API request send-email");
        try {
            int emailSent = mailService.processMailSending();
            return ResponseEntity.ok().body(new CommonResponse(true,
                    new String[]{String.format("Email sending finished. Emails were sent %d", emailSent)}));
        } catch (Exception e) {
            String error = e.getMessage();
            return ResponseEntity.badRequest().body(new CommonResponse(false, new String[]{error}));
        }
    }

    @PutMapping("/detect-chat")
    public ResponseEntity<String> detectChatById(@RequestParam Long chatId) {
        log.info("API request to detect chat");
        detector.detectChat(chatId);
        log.info("Chat " + chatId + " detected.");
        return ResponseEntity.ok("Chat detected");
    }
}
