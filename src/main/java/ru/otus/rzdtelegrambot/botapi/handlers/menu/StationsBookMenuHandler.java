package ru.otus.rzdtelegrambot.botapi.handlers.menu;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.otus.rzdtelegrambot.botapi.BotState;
import ru.otus.rzdtelegrambot.botapi.BotStateContext;
import ru.otus.rzdtelegrambot.botapi.handlers.InputMessageHandler;
import ru.otus.rzdtelegrambot.service.ReplyMessagesService;
import ru.otus.rzdtelegrambot.service.StationBookService;

/**
 * @author Sergei Viacheslaev
 */
@Component
public class StationsBookMenuHandler implements InputMessageHandler {
    private ReplyMessagesService messagesService;
    private StationBookService stationsBookService;
    private BotStateContext botStateContext;

    public StationsBookMenuHandler(ReplyMessagesService messagesService, StationBookService stationsBookService, @Lazy BotStateContext botStateContext) {
        this.messagesService = messagesService;
        this.stationsBookService = stationsBookService;
        this.botStateContext = botStateContext;
    }

    @Override
    public SendMessage handle(Message message) {
        if (botStateContext.getCurrentState().equals(BotState.STATIONS_SEARCH)) {
            botStateContext.setCurrentState(BotState.ASK_STATION_NAMEPART);
            return messagesService.getReplyMessage(message.getChatId(), "reply.stationBookMenu.searchHelpMessage");
        }
        return processUsersInput(message);
    }

    @Override
    public BotState getHandlerName() {
        return BotState.STATIONS_SEARCH;
    }

    private SendMessage processUsersInput(Message inputMsg) {
        String usersInput = inputMsg.getText();
        long chatId = inputMsg.getChatId();

        SendMessage replyToUser = messagesService.getWarningReplyMessage(chatId,"reply.query.failed");
        BotState botState = botStateContext.getCurrentState();


        if (botState.equals(BotState.ASK_STATION_NAMEPART)) {
            replyToUser = stationsBookService.processStationNamePart(chatId, usersInput);
            botStateContext.setCurrentState(BotState.ASK_STATION_NAMEPART);
        }

        return replyToUser;
    }
}
