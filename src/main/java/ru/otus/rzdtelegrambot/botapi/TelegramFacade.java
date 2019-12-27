package ru.otus.rzdtelegrambot.botapi;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.otus.rzdtelegrambot.repository.UserDatabase;

/**
 * @author Sergei Viacheslaev
 */
@Service
@Slf4j
public class TelegramFacade {
    private UserDatabase userDatabase;

    private BotStateContext botStateContext;

    public TelegramFacade(UserDatabase userDatabase, BotStateContext botStateContext) {
        this.userDatabase = userDatabase;
        this.botStateContext = botStateContext;
    }

    public SendMessage handleUpdate(Update update) {
        Message message = update.getMessage();
        SendMessage replyMessage = null;

        if (message != null && message.hasText()) {
            log.info("New message from user:{} with text:{}", message.getFrom().getFirstName(), message.getText());

            replyMessage = handleInputMessage(message);

        }

        return replyMessage;
    }

    private SendMessage handleInputMessage(Message message) {
        String inputMsg = message.getText();
        int userId = message.getFrom().getId();
        BotState botState;
        SendMessage replyMessage;

        switch (inputMsg) {
            case "Найти поезда":
                botState = BotState.TRAINS_SEARCH;
                break;
            case "Помощь":
                botState = BotState.SHOW_HELP_MENU;
                break;
            default:
                botState = userDatabase.getUserBotState(userId);
                break;
        }

        //Назначаем нашему боту полученное состояние
        botStateContext.setCurrentState(botState);

        //Обрабатываем сообщение от пользователя, находясь уже в нужном состоянии
        replyMessage = botStateContext.processInputMessage(message);

        //Сохраняем в БД последнее состояние после обработки сообщения
        userDatabase.saveUserBotState(userId, botStateContext.getCurrentState());

        return replyMessage;
    }
}
