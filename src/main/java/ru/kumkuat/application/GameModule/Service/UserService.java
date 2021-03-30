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

    public long setUserIntoDB(org.telegram.telegrambots.meta.api.objects.User telegramUser) throws Exception {
        if (telegramUser.getUserName() != null) {
            User user = new User();
            user.setName(telegramUser.getUserName());
            user.setFirstName(telegramUser.getFirstName());
            user.setLastName(telegramUser.getLastName());
            user.setSceneId(0l);
            user.setTelegramUserId((long) telegramUser.getId());
            userRepository.save(user);
            return user.getId();
        }
        throw new Exception("User name is null");
    }

    public User getUser(Long id) throws NullPointerException {
        User user = userRepository.getByTelegramUserId(id);
        if (user == null) {
            throw new NullPointerException("User is doesn't exist in DB. NullPointerException.");
        }

        return user;

    }


    public boolean IsUserExist(String name) {
        for (User user :
                userRepository.findAll()) {
            if (user.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    public void incrementSceneId(Long userId) {
        try {
            User userToUpdate = getUser(userId);
            Long sceneId = userToUpdate.getSceneId();
            if (restartScenesCounter(sceneId)) {
                userToUpdate.setSceneId(0l);
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


}
