import org.junit.jupiter.api.Test;
import ru.kumkuat.application.GameModule.Bot.MayakBot;

public class BotTest {



    @Test
    public void op () {
        MayakBot te = new MayakBot();
        System.out.println(te.getBotUsername());
        System.out.println(te.getBotToken());
    }


}

