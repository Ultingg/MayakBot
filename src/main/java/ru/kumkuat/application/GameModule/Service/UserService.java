package ru.kumkuat.application.GameModule.Service;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Service;
import ru.kumkuat.application.GameModule.Models.User;
import ru.kumkuat.application.GameModule.Repository.UserRepository;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.Properties;


@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    public long setUserIntoDB(org.telegram.telegrambots.meta.api.objects.User telegramUser) {
        User user = new User();
        user.setName(telegramUser.getUserName());
        user.setSceneId(0l);
        user.setTelegramUserId((long)telegramUser.getId());
        userRepository.save(user);
        return user.getId();
    }
    public User getUser(Long id) {
        return userRepository.getById(id);
    }
    public User getUser(String name) throws Exception {
        for (User user:
        userRepository.findAll()) {
            if(user.getName().equals(name)){
                return user;
            }
        }
        throw new Exception("User doesn't exist");
    }
    public boolean IsUserExist(String name) {
        for (User user:
                userRepository.findAll()) {
            if(user.getName().equals(name)){
                return true;
            }
        }
        return false;
    }
}
