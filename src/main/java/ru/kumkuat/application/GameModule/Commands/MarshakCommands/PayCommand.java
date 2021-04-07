package ru.kumkuat.application.GameModule.Commands.MarshakCommands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendInvoice;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.payments.LabeledPrice;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.kumkuat.application.GameModule.Service.UserService;

import java.util.ArrayList;
import java.util.List;

@Service
@PropertySource(value = "file:../resources/externalsecret.yml")
public class PayCommand extends BotCommand {
    @Autowired
    private UserService userService;
    @Value("${marshak.payment.provider.token}")
    private String paymentProviderToken;
    private static final String COMMAND_DESCRIPTION = "Так Вы можете оплатить прогулку";

    public PayCommand() {
        super("/pay", COMMAND_DESCRIPTION);
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        SendMessage replyMessage = new SendMessage();
        replyMessage.setChatId(chat.getId().toString());
        replyMessage.enableHtml(true);

        SendInvoice sendInvoice = new SendInvoice();
        sendInvoice.setChatId(chat.getId().intValue());
        sendInvoice.setTitle("Веселые старты");
        sendInvoice.setDescription("Побегаем, попрыгаем, ножками подрыгаем");
        sendInvoice.setPayload("Payload");
        sendInvoice.setProviderToken(paymentProviderToken);
        sendInvoice.setCurrency("RUB");
        sendInvoice.setStartParameter("StartParameter");
        List<LabeledPrice> labeledPrices = new ArrayList<>();
        LabeledPrice labeledPrice = new LabeledPrice();
        labeledPrice.setLabel("Руб");
        labeledPrice.setAmount(10000);
        labeledPrices.add(labeledPrice);
        sendInvoice.setPrices(labeledPrices);
        try {
            var result = absSender.execute(sendInvoice);
            System.out.println("result text:" + result.getInvoice().getTitle());
        } catch (TelegramApiException e) {
        }
    }


}
