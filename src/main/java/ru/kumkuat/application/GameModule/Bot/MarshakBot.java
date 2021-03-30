package ru.kumkuat.application.GameModule.Bot;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerPreCheckoutQuery;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.groupadministration.KickChatMember;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.kumkuat.application.GameModule.Abstract.TelegramWebhookCommandBot;
import ru.kumkuat.application.GameModule.Commands.MarshakCommands.*;


@Setter
@Getter
@Component
@PropertySource(name = "secret.yml", value = "secret.yml")
public class MarshakBot extends TelegramWebhookCommandBot {

    @Value("${marshak.name}")
    private String botUsername;
    @Value("${marshak.token}")
    private String botToken;
    @Value("${marshak.path}")
    private String botPath;

//    @Autowired
//    private PlayCommand playCommand;
//    @Autowired
//    private PayCommand payCommand;
//    @Autowired
//    private ResetCommand resetCommand;
//    @Autowired
//    private SaveChatCommand saveChatCommand;

    private MarshakBot(PlayCommand playCommand, PayCommand payCommand, ResetCommand resetCommand, SaveChatCommand saveChatCommand, KickAllCommand kickAllCommand) {
        register(playCommand);
        register(payCommand);
        register(resetCommand);
        register(saveChatCommand);
        register(kickAllCommand);
    }

    @Override
    public BotApiMethod processNonCommandUpdate(Update update) {
        if(update.hasPreCheckoutQuery()){
            AnswerPreCheckoutQuery answerPreCheckoutQuery = new AnswerPreCheckoutQuery();
            answerPreCheckoutQuery.setPreCheckoutQueryId(update.getPreCheckoutQuery().getId());
            answerPreCheckoutQuery.setOk(true);
            try {
                this.execute(answerPreCheckoutQuery);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public synchronized void sendMsg(String chatId, String s, Integer ID) { //TODO: удалить этот метод???
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(chatId);
        sendMessage.setReplyToMessageId(ID);
        sendMessage.setText(s);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
