package ru.kumkuat.application.gameModule.api.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.kumkuat.application.gameModule.api.services.ChatDetector;
import ru.kumkuat.application.gameModule.mail.SimpleEmailService;
import ru.kumkuat.application.gameModule.models.MessageContainer;
import ru.kumkuat.application.gameModule.service.AdminUserService;
import ru.kumkuat.application.gameModule.service.ForceMessageService;
import ru.kumkuat.application.gameModule.service.XLSXServices.XLSXService;
import ru.kumkuat.application.gameModule.utils.CommonResponse;

/**
 * Class API for sending request to application.
 */
@Slf4j
@RestController
@RequestMapping("/api")
public class ApiController {

    private final XLSXService xlsxService;
    private final AdminUserService adminUserService;
    private final SimpleEmailService mailService;
    private final ForceMessageService forceMessageService;
    private final ChatDetector detector;

    public ApiController(XLSXService xlsxService, AdminUserService adminUserService, SimpleEmailService mailService,
                         ForceMessageService forceMessageService, ChatDetector detector) {
        this.xlsxService = xlsxService;
        this.adminUserService = adminUserService;
        this.mailService = mailService;
        this.forceMessageService = forceMessageService;
        this.detector = detector;
    }

    @GetMapping("/parsetimepad")
    public ResponseEntity<String> parseTimePAdOrder() {
        log.info("API request parsetimepad");
        xlsxService.parseTimePadFile();
        return ResponseEntity.ok("DONE");
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

    @PostMapping("/send-email-time")
    public ResponseEntity<CommonResponse> sendEmailsWithTime() {
        log.info("API request send-email with time");
        try {
            int emailSent = mailService.processMailSendingWithTime();
            return ResponseEntity.ok().body(new CommonResponse(true,
                    new String[]{String.format("Email with time sending finished. Emails were sent %d", emailSent)}));
        } catch (Exception e) {
            String error = e.getMessage();
            return ResponseEntity.badRequest().body(new CommonResponse(false, new String[]{error}));
        }
    }

    @PostMapping("/send-email-promo")
    public ResponseEntity<CommonResponse> sendEmailsWithPromo() {
        log.info("API request send-email promo emails");
        try {
            int emailSent = mailService.sendPromoMail();
            return ResponseEntity.ok().body(new CommonResponse(true,
                    new String[]{String.format("Email with promo sending finished. Emails were sent %d", emailSent)}));
        } catch (Exception e) {
            String error = e.getMessage();
            return ResponseEntity.badRequest().body(new CommonResponse(false, new String[]{error}));
        }
    }

    @PutMapping("/detect-chat")
    public ResponseEntity<String> detectChatById(@RequestParam Long chatId) {
        log.info("API request to detect chat");
        String linkToChat = detector.detectChatByChatId(chatId);
        log.info("Chat " + chatId + " detected.");
        return ResponseEntity.ok("Chat detected. Link: " + linkToChat);
    }

    @GetMapping("/user")
    public ResponseEntity<UserInfo> getUserInfo(@RequestParam Long telegramUserId) {
        log.info("API request getUserInfo");
        UserInfo result = adminUserService.getUserInfo(telegramUserId);
        log.info("userInfo returned by id: " + telegramUserId);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/user/full-restart")
    public ResponseEntity<String> fullRestartUser(@RequestParam Long telegramUserId) {
        log.info("API request full-restart");
        adminUserService.FullRestartUserById(telegramUserId);
        log.info("User restarted by id: " + telegramUserId);
        return ResponseEntity.ok("DONE");
    }

    @PostMapping("/user/restart")
    public ResponseEntity<String> restartUser(@RequestParam Long telegramUserId) {
        log.info("API request restart in chat");
        adminUserService.restartUserById(telegramUserId);
        log.info("User restarted by id: " + telegramUserId);
        return ResponseEntity.ok("DONE");
    }

    @PostMapping("/force-message")
    private ResponseEntity<String> forceMassage(@RequestBody MessageContainer messageContainer) {
        log.info("API request force message in chats");
        forceMessageService.forceMessageToAllChats(messageContainer);
        log.info("API request force message in chats finished");
        return ResponseEntity.ok("DONE");
    }

    @PostMapping("/force-message-one-chat")
    private ResponseEntity<String> forceMassage(@RequestParam("chatId") Long chatId,
                                                @RequestBody MessageContainer messageContainer) {
        log.info("API request force message in chat: " + chatId);
        forceMessageService.forceMessageToDefinedChat(messageContainer, chatId);
        log.info("API request force message in chat: " + chatId + " finished");
        return ResponseEntity.ok("DONE");
    }
}
