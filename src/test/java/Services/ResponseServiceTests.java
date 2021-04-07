package Services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.kumkuat.application.GameModule.Controller.BotController;
import ru.kumkuat.application.GameModule.Service.*;
import ru.kumkuat.application.TemporaryCollections.SceneCollection;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

public class ResponseServiceTests {

    private SceneCollection sceneCollection = Mockito.mock(SceneCollection.class);
    private PictureService pictureService = Mockito.mock(PictureService.class);
    private AudioService audioService = Mockito.mock(AudioService.class);
    private BotController botController = Mockito.mock(BotController.class);
    private GeolocationDatabaseService geolocationDatabaseService = Mockito.mock(GeolocationDatabaseService.class);
    private TriggerService triggerService = Mockito.mock(TriggerService.class);
    @Mock
    private UserService userService;
    private ResponseService responseService;

    @BeforeEach
    public void setUpService() {
        responseService = new ResponseService(sceneCollection, pictureService, audioService,
                botController, geolocationDatabaseService, triggerService, userService);
    }

    @Test
    public void notNullSrevice() {
        assertNotNull(responseService);
    }

    @Test
    void navigationCommandTest() {
        Message incomingMessage = new Message();
        User telegramUser = new User();
        ru.kumkuat.application.GameModule.Models.User DBuser = new ru.kumkuat.application.GameModule.Models.User();
        DBuser.setId(1l);
        DBuser.setSceneId(1l);
        telegramUser.setId(1);
        boolean isCommandMessage = true;
        when(userService.IsUserExist(1l)).thenReturn(true);
        when(incomingMessage.getFrom()).thenReturn(telegramUser);
//        when(incomingMessage.getS)
//        when(sceneCollection.get())
        responseService.messageReceiver(incomingMessage,isCommandMessage);


    }
}
