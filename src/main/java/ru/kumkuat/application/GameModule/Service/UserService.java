package ru.kumkuat.application.GameModule.Service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.kumkuat.application.GameModule.Exceptions.UserServiceException;
import ru.kumkuat.application.GameModule.Models.BGUser;
import ru.kumkuat.application.GameModule.Models.User;
import ru.kumkuat.application.GameModule.Repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class UserService {

    private final UserRepository userRepository;
    private final SceneService sceneService;
    private final BGUserService bgUserService;

    public User getUserByDBId(Long id) {
        return userRepository.getById(id);
    }

    public User getUserByTelegramId(Long telegramId) throws UserServiceException {
        User user = userRepository.getByTelegramUserId(telegramId);
        if (user == null) {
            throw new UserServiceException("User is doesn't exist in DB. UserServiceException.");
        }
        return user;
    }

    public List<User> getAll() {
        List<User> userList = new ArrayList<>();
        userRepository.findAll().iterator().forEachRemaining(userList::add);
        return userList;
    }

    public UserService(UserRepository userRepository, SceneService sceneService, BGUserService bgUserService) {
        this.userRepository = userRepository;
        this.sceneService = sceneService;
        this.bgUserService = bgUserService;
    }

    public void setUserScene(org.telegram.telegrambots.meta.api.objects.User telegramUser, Integer i) {
        if (telegramUser.getUserName() != null) {
                var user = userRepository.getByTelegramUserId(telegramUser.getId().longValue());
                user.setSceneId(i.longValue());
                userRepository.save(user);
        }
    }

    /*возможно стоит сделать этот метод синхронизированным,
     т.к. возможны проблемы при одновременной записи двух и более юзеров под одним id(не telegramID) в бд */
    public long setUserIntoDB(org.telegram.telegrambots.meta.api.objects.User telegramUser) {
        User user = new User();
        if (telegramUser.getUserName() != null) {
            user.setName(badNameConvertingToGoodName(telegramUser.getUserName()));
        } else {
            user.setName("");
        }
        if (telegramUser.getFirstName() != null) {
            user.setFirstName(badNameConvertingToGoodNameForLastFirstName(telegramUser.getFirstName()));
        } else {
            user.setFirstName("");
        }
        if (telegramUser.getLastName() != null) {
            user.setLastName(badNameConvertingToGoodNameForLastFirstName(telegramUser.getLastName()));
        } else {
            user.setLastName("");
        }
        user.setSceneId(0L);
        user.setTelegramUserId((long) telegramUser.getId());
        user.setRegistrationStamp(LocalDateTime.now().plusHours(3));
        userRepository.save(user);
        return user.getId();
    }

    private String badNameConvertingToGoodName(String badName) {
        return badName.replaceAll("\\W", "");
    }

    private String badNameConvertingToGoodNameForLastFirstName(String badName) {
        return badName.replaceAll("[a-zA-zа-яА-Я]", "");
    }

    public void save (User user) {
        userRepository.save(user);
    }

    public boolean isUserPromoByTelegramId(Long userTelegramId) {
        User user = userRepository.getByTelegramUserId(userTelegramId);
        return user.isPromo();
    }

    public void setUserPromoFlag(Long userTelegramId, boolean promoFlag) {
        User user = userRepository.getByTelegramUserId(userTelegramId);
        if (user != null) {
            user.setPromo(promoFlag);
            userRepository.save(user);
        } else {
            throw new NullPointerException("User is null");
        }
    }

    public boolean IsUserExist(Long telegramId) {
        return userRepository.getByTelegramUserId(telegramId) != null;
    }

    public void incrementSceneId(Long userId) {
            User userToUpdate = getUserByTelegramId(userId);
            Long sceneId = userToUpdate.getSceneId();

            if (isSceneHaveLastNumberOrMore(sceneId) && userToUpdate.isAdmin()) {
                userToUpdate.setSceneId(0L);
            } else {
                userToUpdate.setSceneId(sceneId + 1);
            }
            userRepository.save(userToUpdate);
    }
    public boolean isSceneHaveLastNumberOrMore(Long sceneId) {
        Long sceneSize = (long) sceneService.count();
        return sceneId >= sceneSize - 1;

    }

    public boolean IsUserHasPayment(long TelegramId) {
        User user = getUserByTelegramId(TelegramId);
            return user.isHasPay();
    }

    public void setUserPayment(long TelegramId, boolean isPay) {
        User user = getUserByTelegramId(TelegramId);
            user.setHasPay(isPay);
            userRepository.save(user);
    }

    public void setPlaying(long TelegramId, boolean isPlaying) {
        User user = getUserByTelegramId(TelegramId);
            user.setPlaying(isPlaying);
            userRepository.save(user);
    }

    public boolean setUserNickName(Long telegramId, String nickName) {
        User user = userRepository.getByTelegramUserId(telegramId);
        user.setNickName(nickName);
        userRepository.save(user);
        return nickName.equals(userRepository.getByTelegramUserId(telegramId).getNickName());
    }

    public boolean setUserTrigger(Long telegramId, boolean trigger) {
        User user = userRepository.getByTelegramUserId(telegramId);
        user.setTriggered(trigger);
        userRepository.save(user);
        return trigger == userRepository.getByTelegramUserId(telegramId).isTriggered();
    }


    public boolean validateUsersAndBGUsers(String username) {
        List<BGUser> bgUsers = bgUserService.getAll();
        return bgUsers.stream()
                .anyMatch(bgUser -> bgUser.getTelegramUserName().toLowerCase().equals(username.toLowerCase()));
    }

    public void saveUser(User player) {
        userRepository.save(player);
    }
}
