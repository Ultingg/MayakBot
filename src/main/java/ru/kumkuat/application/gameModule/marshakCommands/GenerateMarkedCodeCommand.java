package ru.kumkuat.application.gameModule.marshakCommands;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.kumkuat.application.gameModule.promocode.Model.DisposablePromocode;
import ru.kumkuat.application.gameModule.promocode.Service.PromocodeService;
import ru.kumkuat.application.gameModule.service.UserService;

@Slf4j
@Component
public class GenerateMarkedCodeCommand extends BotCommand implements AdminCommand {

    private final UserService userService;
    private final PromocodeService promocodeService;

    public GenerateMarkedCodeCommand(UserService userService, PromocodeService promocodeService) {
        super("/code", "Generate promocode");
        this.userService = userService;
        this.promocodeService = promocodeService;
    }


    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        SendMessage replyMessage = new SendMessage();
        replyMessage.setChatId(chat.getId().toString());
        replyMessage.enableHtml(true);
        Long userId = Long.valueOf(user.getId());


        if (userService.getUserByTelegramId(userId).isAdmin()) {
            if (strings != null && strings.length > 0) {
                String amount = strings[0];
                replyMessage.setText(getListOfPromocodes(amount));
            } else {
                replyMessage.setText(getOnePromocode());
            }
        } else {
            replyMessage.setText("Вы не обладаете соответствующим уровнем доступа.");
        }

        execute(absSender, replyMessage, user);
    }

    private String getOnePromocode() {
        DisposablePromocode newPromocode = promocodeService.getNewDisposalMarkedPromocode();
        if (newPromocode != null && newPromocode.getValue() != null) {
            log.info("Promocode was generated and sent to admin.");
            return String.format("Сгенерирован новый промокод: %s", newPromocode.getValue());
        } else {
            log.error("Error while generating promocod and peristing it.");
            return "Произошла оршибка вов время генерации промокода...смотри логи, лапоть";
        }
    }

    private String getListOfPromocodes(String amount) {
        int qty = Integer.parseInt(amount);
        log.info("Start generating list of promocodes, amount: {}.", amount);
        StringBuilder listOfCodes = new StringBuilder();
        for (int i = 0; i < qty; i++) {
            DisposablePromocode newPromocode = promocodeService.getNewDisposalMarkedPromocode();
            if (newPromocode != null && newPromocode.getValue() != null) {
                listOfCodes.append(newPromocode.getValue() + "\n");
            } else {
                log.error("Error while generating promocod and peristing it.");

            }
        }
        return listOfCodes.toString();
    }


    void execute(AbsSender sender, SendMessage message, User user) {
        try {
            sender.execute(message);
        } catch (TelegramApiException e) {
            log.error("Error while executing GeneratePromocodeCommand.", e);
        }
    }
}
