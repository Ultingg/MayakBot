package services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.kumkuat.application.GameModule.Models.BGUser;
import ru.kumkuat.application.GameModule.Repository.BGUserRepository;
import ru.kumkuat.application.GameModule.Service.BGUserService;

import java.time.LocalTime;

public class BGUsersServiceTest {


    @InjectMocks
    private BGUserService bgUserService;

    @Mock
    private BGUserRepository bgUserRepository;

    @BeforeEach
    void init(){
        MockitoAnnotations.initMocks(this);
    }


    @Test
    void calculateStartTime_settingCorrectTime() {
        BGUser bgUser = BGUser.builder()
                .preferredTime("с 14:00 до 14:30").build();
        LocalTime expected = LocalTime.of(14,0);

        LocalTime actual = bgUserService.calculateStartTime(bgUser);

        Assertions.assertEquals(expected, actual);
    }




}
