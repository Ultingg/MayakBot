package ru.kumkuat.application.GameModule.Service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.kumkuat.application.GameModule.Models.User;
import ru.kumkuat.application.GameModule.Repository.UserRepository;

@Slf4j
@Service
public class UserService {

    private final UserRepository userRepository;
    private final SceneService sceneService;

    private User getUserByTelegramId(Long id) throws NullPointerException {
        User result = userRepository.getByTelegramUserId(id);
        if (result == null) throw new NullPointerException("User not found in DB!");
        return result;
    }

    public UserService(UserRepository userRepository, SceneService sceneService) {
        this.userRepository = userRepository;
        this.sceneService = sceneService;
    }

    public void setUserScene(org.telegram.telegrambots.meta.api.objects.User telegramUser, Integer i) {
        if (telegramUser.getUserName() != null) {
            try {
                var user = userRepository.getByTelegramUserId(telegramUser.getId().longValue());
                user.setSceneId(i.longValue());
                userRepository.save(user);
            } catch (NullPointerException e) {
                e.getMessage();
                log.debug("User is null.");
            }
        }
    }

    /*возможно стоит сделать этот метод синхронизированным,
     т.к. возможны проблемы при одновременной записи двух и более юзеров под одним id(не telegramID) в бд */
    public long setUserIntoDB(org.telegram.telegrambots.meta.api.objects.User telegramUser) throws Exception {
        User user = new User();
        if (telegramUser.getUserName() != null) {
            user.setName(badNameConvertingToGoodName(telegramUser.getUserName()));
        } else {
            user.setName("");
        }
        if (telegramUser.getFirstName() != null) {
            user.setFirstName(badNameConvertingToGoodName(telegramUser.getFirstName()));
        } else {
            user.setFirstName("");
        }
        if (telegramUser.getLastName() != null) {
            user.setLastName(badNameConvertingToGoodName(telegramUser.getLastName()));
        } else {
            user.setLastName("");
        }
        user.setSceneId(0L);
        user.setTelegramUserId((long) telegramUser.getId());
        userRepository.save(user);
        return user.getId();
        //throw new Exception("User name is null");
    }
    private String badNameConvertingToGoodName(String badName) {
        String goodName = badName.replaceAll("\\W", "");
        return goodName;

    }

    public User getUser(Long telegramId) throws NullPointerException {
        User user = userRepository.getByTelegramUserId(telegramId);
        if (user == null) {
            throw new NullPointerException("User is doesn't exist in DB. NullPointerException.");
        }
        return user;
    }


    public boolean IsUserExist(Long telegramId) {
        return userRepository.getByTelegramUserId(telegramId) != null;
    }

    public void incrementSceneId(Long userId) {
        try {
            User userToUpdate = getUser(userId);
            Long sceneId = userToUpdate.getSceneId();

            if (restartScenesCounter(sceneId)) {
                if (userToUpdate.isAdmin()) {
                    userToUpdate.setSceneId(0L);
                } else {
                    userToUpdate.setSceneId(0L);
                }
            } else {
                userToUpdate.setSceneId(sceneId + 1);
            }
            userRepository.save(userToUpdate);
        } catch (NullPointerException ex) {
            ex.getMessage();
        }
    }

    /* if(User == Maksim/Nikolay) привязать проверку к нашим telegramId не надо никаких проверок админа...
    хотя это плохо тем что если нужен будет новый админ, надо лезть в код программы.
    Пока вижу два решения:
    1) флаг у Юзера isAdmin
    2) отдельная таблица в БД для админов, куда ручками можно добавить админа
    обдумать а как будет происходить добавления админа и в какой момент
    */
    private boolean restartScenesCounter(Long sceneId) { //наша халява обнуляет счетчик сцен
        //читай коммент вверху ;)
        Long sceneSize = Long.valueOf(sceneService.count());
        return sceneId >= sceneSize - 1;

    }

    public boolean IsUserHasPayment(long TelegramId) {
        User user = getUserByTelegramId(TelegramId);
        if (user != null) {
            return user.isHasPay();

        }
        throw new NullPointerException("User is null");
    }

    public void setUserPayment(long TelegramId, boolean isPay) {
        User user = getUserByTelegramId(TelegramId);
        if (user != null) {
            user.setHasPay(isPay);
            userRepository.save(user);
        } else {
            throw new NullPointerException("User is null");
        }
    }

    public void setPlaying(long TelegramId, boolean isPlaying) {
        User user = getUserByTelegramId(TelegramId);
        if (user != null) {
            user.setPlaying(isPlaying);
            userRepository.save(user);
        } else {
            throw new NullPointerException("User is null");
        }
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
}
