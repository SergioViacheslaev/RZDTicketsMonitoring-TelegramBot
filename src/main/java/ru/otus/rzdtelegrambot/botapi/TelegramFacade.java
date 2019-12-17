package ru.otus.rzdtelegrambot.botapi;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.otus.rzdtelegrambot.botapi.handlers.InputCommandHandler;
import ru.otus.rzdtelegrambot.service.MainMenuService;
import ru.otus.rzdtelegrambot.session.UserState;
import ru.otus.rzdtelegrambot.session.UsersChatContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Sergei Viacheslaev
 */
@Service
@Slf4j
public class TelegramFacade {
    private Map<String, InputCommandHandler> messageCommands = new HashMap<>();

    @Autowired
    private UsersChatContext chatContext;

    @Autowired
    private MainMenuService mainMenuService;

    public TelegramFacade(List<InputCommandHandler> commandHandlers) {
        commandHandlers.forEach(handler -> messageCommands.put(handler.getHandlerName(), handler));
    }

    public SendMessage handleUpdate(Update update) {
        Message message = update.getMessage();
        SendMessage replyMessage = null;

        if (message != null && message.hasText()) {
            log.info("New message from user:{} with text:{}", message.getText(), message.getFrom().getFirstName());

            InputCommandHandler messageHandler = messageCommands.get(message.getText());
            if (messageHandler != null) {
                replyMessage = messageHandler.handle(message);
                return replyMessage;
            }

            return processUsersChatContext(message);
        }

        return replyMessage;

    }

    private SendMessage processUsersChatContext(Message message) {
        int userId = message.getFrom().getId();
        UserState userState = chatContext.getUserStateByID(userId);

        if (userState == null) {
            return mainMenuService.getMainMenuMessage(message);
        }


        if (userState.equals(UserState.SHOW_MAIN_MENU)) {
            return mainMenuService.getMainMenuMessage(message);
        }

        boolean result = isTrainSearchState(userState);
        if (isTrainSearchState(userState)) {

            return messageCommands.get("Найти поезда").handle(message);

        }

        return null;


    }


    private boolean isTrainSearchState(UserState userState) {
        switch (userState) {
            case ASK_DATE_DEPART:
            case ASK_STATION_ARRIVAL:
            case ASK_STATION_DEPART:
            case DATE_DEPART_RECEIVED:
            case TRAIN_INFO_RESPONCE_AWAITING:
                return true;
            default:
                return false;
        }
    }

}
