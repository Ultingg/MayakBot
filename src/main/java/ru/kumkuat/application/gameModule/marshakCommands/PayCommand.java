package ru.kumkuat.application.gameModule.marshakCommands;

import lombok.extern.slf4j.Slf4j;
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
import ru.kumkuat.application.gameModule.exceptions.TelegramCommandException;
import ru.kumkuat.application.gameModule.repository.UserRepository;
import ru.kumkuat.application.gameModule.service.TelegramChatService;
import ru.kumkuat.application.gameModule.service.UserService;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@PropertySource(value = "file:../resources/externalsecret.yml")
public class PayCommand extends BotCommand {
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Value("${price.general}")
    private Integer generalPrice;
    @Value("${price.promo}")
    private Integer promoPrice;
    @Autowired
    private TelegramChatService telegramChatService;

    @Value("${marshak.payment.provider.token}")
    private String paymentProviderToken;
    private static final String COMMAND_DESCRIPTION = "Так Вы можете оплатить прогулку";

    public PayCommand() {
        super("/pay", COMMAND_DESCRIPTION);
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        Long userId = user.getId().longValue();
        if (arguments != null && arguments.length > 0) {
            try {
                userId = Long.parseLong(arguments[0]);
                var player = userService.getUserByTelegramId(userId);
                player.setHasPay(!player.isHasPay());
                userRepository.save(player);
                SendMessage replyMessage = new SendMessage();
                replyMessage.setChatId(chat.getId().toString());
                replyMessage.enableHtml(true);
                replyMessage.setText("Оплата успешно проведена!");

                absSender.execute(replyMessage);
                sendInfoMessageToAdmin(absSender, user.getId().longValue());
            } catch (TelegramApiException e) {
                e.printStackTrace();
                log.info("Exception while executing pay command, when pay is success.");
            }
        } else {
            SendMessage replyMessage = new SendMessage();
            replyMessage.setChatId(chat.getId().toString());
            replyMessage.enableHtml(true);

            SendInvoice sendInvoice = new SendInvoice();
            sendInvoice.setChatId(chat.getId());
            sendInvoice.setTitle("ПроСпект");
            sendInvoice.setDescription("Городской спектакль по следам петербургских поэтов");
            sendInvoice.setPayload("Payload");
            sendInvoice.setProviderToken(paymentProviderToken);
            sendInvoice.setCurrency("RUB");
            sendInvoice.setStartParameter("StartParameter");
            List<LabeledPrice> labeledPrices = new ArrayList<>();
            LabeledPrice labeledPrice = new LabeledPrice();
            labeledPrice.setLabel("Руб");
            labeledPrice.setAmount(getActualPriceForCurrentUser(userId));
            labeledPrices.add(labeledPrice);
            sendInvoice.setPrices(labeledPrices);
            try {
                var result = absSender.execute(sendInvoice);
                System.out.println("result text:" + result.getInvoice().getTitle());
                log.info("Invoice sent to {}", chat.getId());
            } catch (TelegramApiException e) {
                log.info("Exception on creation of invoice ", e);
            }
        }
    }

    private Integer getActualPriceForCurrentUser(Long id) {
        var currentUser = userService.getUserByTelegramId(id);
        return currentUser.isPromo() ? promoPrice : generalPrice;
    }

    public void sendInfoMessageToAdmin(AbsSender absSender, Long userTelegeramId) {
        try {
            if (userService.IsUserExist(userTelegeramId)) {
                var userdb = userService.getUserByTelegramId(userTelegeramId);

                String reply = "Пользователь:\n";
                reply += "юзер телеграм id: " + userdb.getTelegramUserId() + "\n";
                reply += "юзер id: " + userdb.getId() + "\n";
                reply += "юзер совершил оплату!";

                SendMessage sendMessage = new SendMessage();
                sendMessage.enableHtml(true);
                sendMessage.setChatId(telegramChatService.getAdminChatId());
                sendMessage.setText(reply);
                execute(absSender, sendMessage);
            }
        } catch (TelegramCommandException e) {
            e.printStackTrace();
            e.getLogMessage(this, "when sending message to Admin chat..");
        }
    }

    private void execute(AbsSender absSender, SendMessage sendMessage) throws TelegramCommandException {
        try {
            absSender.execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new TelegramCommandException();
        }
    }

}
