package ru.kumkuat.application.GameModule.Service;

import org.springframework.stereotype.Service;
import ru.kumkuat.application.GameModule.Models.User;
import ru.kumkuat.application.GameModule.Repository.UserRepository;


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

    public long setUserIntoDB(org.telegram.telegrambots.meta.api.objects.User telegramUser) {
        User user = new User();
        user.setName(telegramUser.getUserName());
        user.setSceneId(0l);
        user.setTelegramUserId((long) telegramUser.getId());
        userRepository.save(user);
        return user.getId();
    }

    public User getUser(Long id) {
        return userRepository.getByTelegramUserId(id);
    }

    public User getUser(String name) throws Exception {
        for (User user :
                userRepository.findAll()) {
            if (user.getName().equals(name)) {
                return user;
            }
        }
        throw new Exception("User doesn't exist");
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
        User userToUpdate = getUser(userId);
        Long sceneId = userToUpdate.getSceneId();
        if (restartScenes(sceneId)) {
            userToUpdate.setSceneId(0l);
        } else {
            userToUpdate.setSceneId(sceneId + 1);
        }
        userRepository.save(userToUpdate);
    }

    private boolean restartScenes(Long sceneId) { //наша халява
        Long sceneSize = Long.valueOf(sceneService.count());
        return sceneId >= sceneSize;

    }


}
