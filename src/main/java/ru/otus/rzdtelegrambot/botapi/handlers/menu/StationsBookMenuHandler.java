package ru.otus.rzdtelegrambot.botapi.handlers.menu;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.otus.rzdtelegrambot.botapi.BotState;
import ru.otus.rzdtelegrambot.botapi.handlers.InputMessageHandler;
import ru.otus.rzdtelegrambot.cache.UserDataCache;
import ru.otus.rzdtelegrambot.service.ReplyMessagesService;
import ru.otus.rzdtelegrambot.service.StationBookService;


@Component
public class StationsBookMenuHandler implements InputMessageHandler {
    private ReplyMessagesService messagesService;
    private StationBookService stationsBookService;
    private UserDataCache userDataCache;

    public StationsBookMenuHandler(ReplyMessagesService messagesService, StationBookService stationsBookService,
                                   UserDataCache userDataCache) {
        this.messagesService = messagesService;
        this.stationsBookService = stationsBookService;
        this.userDataCache = userDataCache;
    }

    @Override
    public SendMessage handle(Message message) {
        if (userDataCache.getUsersCurrentBotState(message.getFrom().getId()).equals(BotState.STATIONS_SEARCH)) {
            userDataCache.setUsersCurrentBotState(message.getFrom().getId(), BotState.ASK_STATION_NAMEPART);
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
        Integer userId = inputMsg.getFrom().getId();

        SendMessage replyToUser = messagesService.getWarningReplyMessage(chatId, "reply.query.failed");
        BotState botState = userDataCache.getUsersCurrentBotState(userId);

        if (botState.equals(BotState.ASK_STATION_NAMEPART)) {
            replyToUser = stationsBookService.processStationNamePart(chatId, usersInput);
            userDataCache.setUsersCurrentBotState(userId, BotState.ASK_STATION_NAMEPART);
        }

        return replyToUser;
    }
}
