package ru.kumkuat.application.gameModule.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.kumkuat.application.gameModule.api.controller.UserInfo;
import ru.kumkuat.application.gameModule.api.services.ChatDetector;
import ru.kumkuat.application.gameModule.exceptions.TelegramChatServiceException;
import ru.kumkuat.application.gameModule.models.User;
import ru.kumkuat.application.gameModule.repository.UserRepository;
import ru.kumkuat.application.gameModule.service.mapper.UserInfoMapper;

@Service
public class AdminUserService {
    private final static Logger logger = LoggerFactory.getLogger(AdminUserService.class);
    private final UserRepository userRepository;
    private final TelegramChatService telegramChatService;
    private final ChatDetector chatDetector;
    private final UserInfoMapper userInfoMapper;

    public AdminUserService(UserRepository userRepository, TelegramChatService telegramChatService, ChatDetector chatDetector, UserInfoMapper userInfoMapper) {
        this.userRepository = userRepository;
        this.telegramChatService = telegramChatService;
        this.chatDetector = chatDetector;
        this.userInfoMapper = userInfoMapper;
    }

    public UserInfo getUserInfo(Long telegramUserId) {
        logger.info("Getting info by userId: " + telegramUserId);
        UserInfo userInfo = null;
        try {
            User user = userRepository.getByTelegramUserId(telegramUserId);
            String link = chatDetector.detectChatByTelegramUserId(telegramUserId);
            userInfo = userInfoMapper.toUserInfo(user);
            userInfoMapper.setLink(userInfo, link);
        } catch (TelegramChatServiceException e) {
            logger.info(e.getMessage());
        } catch (Exception e) {
            logger.info("UserInfo collecting error: " + e.getMessage());
        }
        logger.info("Getting info by userId: " + telegramUserId + " done!");
        return userInfo;
    }

    public void FullRestartUserById(Long telegramUserId) {
        logger.info("Full restart by userId: " + telegramUserId);
        restartUser(telegramUserId, 0L);
        telegramChatService.cleanChatByUserTelegramId(telegramUserId);
        logger.info("User fully restarted by userId: " + telegramUserId);
    }

    public void restartUserById(Long telegramUserId) {
        logger.info("Restarting by userId: " + telegramUserId);
        restartUser(telegramUserId, 1L);
        logger.info("User restarted by userId: " + telegramUserId);
    }

    private void restartUser(Long telegramUserId, Long sceneId) {
        User user = userRepository.getByTelegramUserId(telegramUserId);
        user.setTriggered(false);
        user.setSceneId(sceneId);
        user.setPlaying(false);
        userRepository.save(user);
    }
}
