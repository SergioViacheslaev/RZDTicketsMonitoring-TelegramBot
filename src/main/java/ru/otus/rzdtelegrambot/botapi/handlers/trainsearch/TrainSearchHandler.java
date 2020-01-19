package ru.otus.rzdtelegrambot.botapi.handlers.trainsearch;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.otus.rzdtelegrambot.botapi.BotState;
import ru.otus.rzdtelegrambot.botapi.BotStateContext;
import ru.otus.rzdtelegrambot.botapi.handlers.InputMessageHandler;
import ru.otus.rzdtelegrambot.cache.UserDataCache;
import ru.otus.rzdtelegrambot.model.Train;
import ru.otus.rzdtelegrambot.service.ReplyMessagesService;
import ru.otus.rzdtelegrambot.service.SendTicketsInfoService;
import ru.otus.rzdtelegrambot.service.StationCodeService;
import ru.otus.rzdtelegrambot.service.TrainTicketsGetInfoService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


/**
 * Формирует  запрос на поиск поездов,
 * сохраняет и обрабатывает ввод пользователя.
 *
 * @author Sergei Viacheslaev
 */

@Slf4j
@Component
public class TrainSearchHandler implements InputMessageHandler {

    private UserDataCache userDataCache;
    private BotStateContext botStateContext;
    private TrainTicketsGetInfoService trainTicketsService;
    private StationCodeService stationCodeService;
    private SendTicketsInfoService sendTicketsInfoService;
    private ReplyMessagesService messagesService;

    public TrainSearchHandler(UserDataCache userDb, @Lazy BotStateContext botStateContext,
                              TrainTicketsGetInfoService trainTicketsService, StationCodeService stationCodeService,
                              ReplyMessagesService messagesService,
                              SendTicketsInfoService sendTicketsInfoService) {
        this.userDataCache = userDb;
        this.botStateContext = botStateContext;
        this.trainTicketsService = trainTicketsService;
        this.stationCodeService = stationCodeService;
        this.sendTicketsInfoService = sendTicketsInfoService;
        this.messagesService = messagesService;
    }

    @Override
    public SendMessage handle(Message message) {
        if (botStateContext.getCurrentState().equals(BotState.TRAINS_SEARCH)) {
            botStateContext.setCurrentState(BotState.ASK_STATION_DEPART);
        }
        return processUsersInput(message);
    }

    @Override
    public BotState getHandlerName() {
        return BotState.TRAINS_SEARCH;
    }

    private SendMessage processUsersInput(Message inputMsg) {
        String usersAnswer = inputMsg.getText();
        int userId = inputMsg.getFrom().getId();
        long chatId = inputMsg.getChatId();
        SendMessage replyToUser = messagesService.getWarningReplyMessage(chatId, "reply.trainSearch.tryAgain");

        TrainSearchRequestData requestData = userDataCache.getUserTrainSearchData(userId);

        BotState botState = botStateContext.getCurrentState();

        if (botState.equals(BotState.ASK_STATION_DEPART)) {
            replyToUser = messagesService.getReplyMessage(chatId, "reply.trainSearch.enterStationDepart");
            botStateContext.setCurrentState(BotState.ASK_STATION_ARRIVAL);
        }

        if (botState.equals(BotState.ASK_STATION_ARRIVAL)) {
            int departureStationCode = stationCodeService.getStationCode(usersAnswer);
            if (departureStationCode == -1) {
                return messagesService.getWarningReplyMessage(chatId, "reply.trainSearch.stationNotFound");
            }

            requestData.setDepartureStationCode(departureStationCode);
            replyToUser = messagesService.getReplyMessage(chatId, "reply.trainSearch.enterStationArrival");
            botStateContext.setCurrentState(BotState.ASK_DATE_DEPART);
        }

        if (botState.equals(BotState.ASK_DATE_DEPART)) {
            int arrivalStationCode = stationCodeService.getStationCode(usersAnswer);
            if (arrivalStationCode == -1) {
                return messagesService.getWarningReplyMessage(chatId, "reply.trainSearch.stationNotFound");
            }

            requestData.setArrivalStationCode(arrivalStationCode);
            replyToUser = messagesService.getReplyMessage(chatId, "reply.trainSearch.enterDateDepart");
            botStateContext.setCurrentState(BotState.DATE_DEPART_RECEIVED);
        }

        if (botState.equals(BotState.DATE_DEPART_RECEIVED)) {
            Date dateDepart;
            try {
                dateDepart = new SimpleDateFormat("dd.MM.yyyy").parse(usersAnswer);
            } catch (ParseException e) {
                return messagesService.getWarningReplyMessage(chatId, "reply.trainSearch.wrongTimeFormat");
            }
            requestData.setDateDepart(dateDepart);


            List<Train> trainList = trainTicketsService.getTrainTicketsList(chatId, requestData.getDepartureStationCode(),
                    requestData.getArrivalStationCode(), dateDepart);

            if (trainList.isEmpty()) {
                return messagesService.getReplyMessage(chatId, "reply.trainSearch.enterDateDepart");
            }

            sendTicketsInfoService.sendTrainTicketsInfo(chatId, trainList);

            botStateContext.setCurrentState(BotState.SHOW_MAIN_MENU);

            replyToUser = messagesService.getTrainSearchFinishedOKMessage(chatId, "reply.trainSearch.finishedOK");

        }

        userDataCache.saveTrainSearchData(userId, requestData);
        return replyToUser;
    }


}



