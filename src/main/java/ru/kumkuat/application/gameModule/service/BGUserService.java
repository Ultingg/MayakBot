package ru.kumkuat.application.gameModule.service;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import ru.kumkuat.application.gameModule.models.BGUser;
import ru.kumkuat.application.gameModule.models.User;
import ru.kumkuat.application.gameModule.repository.BGUserRepository;
import ru.kumkuat.application.gameModule.repository.UserRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@PropertySource(value = "file:../resources/externalsecret.yml")
public class BGUserService {


    private final BGUserRepository bgUserRepository;
    private final UserRepository userRepository;

    @Value("${start.time}")
    private String startDateResource;

    public BGUserService(BGUserRepository bgUserRepository, UserRepository userRepository) {
        this.bgUserRepository = bgUserRepository;
        this.userRepository = userRepository;
    }

    public void setBGUserToDB(BGUser bgUser) {
        bgUserRepository.save(bgUser);
    }

    public BGUser getBGUserByUsername(String telegramUsername) {
        return bgUserRepository.findBGUserByTelegramUserName(telegramUsername);
    }

    public boolean isBGUserExistByUsername(String telegramUsername) {
        return (bgUserRepository.findBGUserByTelegramUserName(telegramUsername) != null);
    }

    public List<BGUser> getAll() {
        List<BGUser> bgUserList = new ArrayList<>();
        bgUserRepository.findAll().iterator().forEachRemaining(bgUserList::add);
        return bgUserList;
    }

    public void calculateAndSetStartTimeForBGUser(BGUser bgUser) {
        LocalTime finalStartTime;
        if (bgUser.getStartWith() != null && !bgUser.getStartWith().equals("")) {
            if (bgUserRepository.existsBGUserByTelegramUserName(bgUser.getStartWith())) {
                finalStartTime = getTimeOfPreferredFriend(bgUser.getStartWith());
            } else {
                finalStartTime = setTimeByDefaultSchema(bgUser);
            }
        } else {
            finalStartTime = setTimeByDefaultSchema(bgUser);
        }
        bgUser.setStartTime(finalStartTime);
        System.out.println(bgUser.getTelegramUserName() + ": start time - " + finalStartTime);
        bgUserRepository.save(bgUser);
        BGUser fromDB = bgUserRepository.findBGUserByTelegramUserName(bgUser.getTelegramUserName());

        System.out.println(fromDB.getTelegramUserName() + ": start time - " + fromDB.getStartTime());
    }

    private LocalTime setTimeByDefaultSchema(BGUser bgUser) {
        LocalTime startTime = calculateStartTime(bgUser);
        return insertInSchedule(startTime);
    }

    private LocalTime getTimeOfPreferredFriend(String username) {
        return bgUserRepository.getTimeByUsername(username);

    }

    private LocalTime insertInSchedule(LocalTime rawStart) {
        LocalTime temporaryStartTime = rawStart;
        boolean check = bgUserRepository.existsBGUserByStartTime(temporaryStartTime);
        while (check) {
            temporaryStartTime = temporaryStartTime.plusMinutes(2);
            check = bgUserRepository.existsBGUserByStartTime(temporaryStartTime);
        }
        return temporaryStartTime;
    }

    public LocalTime calculateStartTime(BGUser bgUser) {
        String startTime = bgUser.getPreferredTime();
        return LocalTime.parse(startTime.substring(2, 7));
    }

    public boolean isItTimeToStart(String username) {
        LocalTime startTimeOfUser = bgUserRepository.getTimeByUsername(username);
        LocalDateTime startDateTime = LocalDateTime.of(getDate(), startTimeOfUser);
        LocalDateTime currentTime = LocalDateTime.now();
        return currentTime.isAfter(startDateTime);
    }

    public String getTimeStartMessageForUser(String username) {
        LocalTime startTimeOfUser = bgUserRepository.getTimeByUsername(username);
        return String.format("Добрый день! \n" +
                "Время вашего старта: \n%s 31 июля 2021 года. \n" +
                "Приходите к этому времени на старт.", startTimeOfUser);
    }

    private LocalDate getDate() {
        int year = Integer.parseInt(startDateResource.substring(0, 4));
        int month = Integer.parseInt(startDateResource.substring(5, 7));
        int day = Integer.parseInt(startDateResource.substring(8));
        return LocalDate.of(year, month, day);
    }

    public List<BGUser> getListOfUnregistratedBGUsers() {
        List<User> users =  new ArrayList<>();
        userRepository.findAll().forEach(users::add);
        List<BGUser> bgUsers = new ArrayList<>();
        bgUserRepository.findAll().forEach(bgUsers::add);

        List<String> usersNames = users.stream()
                .map(element->element.getName().toLowerCase())
                .collect(Collectors.toList());
        List<BGUser> diffBGUsers = bgUsers.stream()
                .filter(element -> !usersNames.contains(element.getTelegramUserName().toLowerCase()))
                .collect(Collectors.toList());
        return diffBGUsers;
    }

    public List<BGUser> getAllNotNotifedUsers() {
        List<BGUser> bgUserList = new ArrayList<>();
        getAll().stream().filter(bgUser -> bgUser.getIsNotified().equals(false)).iterator().forEachRemaining(bgUserList::add);
        return bgUserList;
    }
}
