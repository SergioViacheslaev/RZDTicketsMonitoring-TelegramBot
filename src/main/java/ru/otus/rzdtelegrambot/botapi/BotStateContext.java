package ru.otus.rzdtelegrambot.botapi;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.otus.rzdtelegrambot.botapi.handlers.InputMessageHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Stores current state of the Bot.
 *  Defines message handlers for each state.
 *
 * @author Sergei Viacheslaev
 */
@Component
public class BotStateContext {
    private BotState currentState;
    private InputMessageHandler currentMessageHandler;
    private Map<BotState, InputMessageHandler> messageHandlers = new HashMap<>();

    public BotStateContext(List<InputMessageHandler> messageHandlers) {
        messageHandlers.forEach(handler -> this.messageHandlers.put(handler.getHandlerName(), handler));
    }

    public BotState getCurrentState() {
        return currentState;
    }

    public void setCurrentState(BotState currentState) {
        this.currentState = currentState;
    }

    public SendMessage processInputMessage(Message message) {

        currentMessageHandler = findMessageHandler(currentState);

        return currentMessageHandler.handle(message);

    }

    private InputMessageHandler findMessageHandler(BotState currentState) {
        if (isTrainSearchState(currentState)) {
            return messageHandlers.get(BotState.TRAINS_SEARCH);
        }

        return messageHandlers.get(currentState);
    }

    private boolean isTrainSearchState(BotState currentState) {
        switch (currentState) {
            case TRAINS_SEARCH:
            case ASK_DATE_DEPART:
            case DATE_DEPART_RECEIVED:
            case ASK_STATION_ARRIVAL:
            case ASK_STATION_DEPART:
            case TRAINS_SEARCH_STARTED:
            case TRAIN_INFO_RESPONCE_AWAITING:
            case TRAINS_SEARCH_FINISH:
                return true;
            default:
                return false;
        }
    }

}





