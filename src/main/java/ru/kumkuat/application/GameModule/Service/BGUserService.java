package ru.kumkuat.application.GameModule.Service;


import org.springframework.stereotype.Service;
import ru.kumkuat.application.GameModule.Models.BGUser;
import ru.kumkuat.application.GameModule.Repository.BGUserRepository;

@Service
public class BGUserService {


    private final BGUserRepository bgUserRepository;


    public BGUserService(BGUserRepository bgUserRepository) {
        this.bgUserRepository = bgUserRepository;
    }



    public void setBGUserToDB(BGUser bgUser) {
      bgUserRepository.save(bgUser);

    }
}
