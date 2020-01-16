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
import ru.otus.rzdtelegrambot.service.SendTicketsInfoService;
import ru.otus.rzdtelegrambot.service.StationCodeService;
import ru.otus.rzdtelegrambot.service.TrainTicketsInfoService;
import ru.otus.rzdtelegrambot.utils.Emojis;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


/**
 * @author Sergei Viacheslaev
 */

@Slf4j
@Component
public class TrainSearchHandler implements InputMessageHandler {

    private UserDataCache userDataCache;
    private BotStateContext botStateContext;
    private TrainTicketsInfoService trainTicketsService;
    private StationCodeService stationCodeService;
    private SendTicketsInfoService sendTicketsInfoService;

    public TrainSearchHandler(UserDataCache userDb, @Lazy BotStateContext botStateContext,
                              TrainTicketsInfoService trainTicketsService, StationCodeService stationCodeService,
                              SendTicketsInfoService sendTicketsInfoService) {
        this.userDataCache = userDb;
        this.botStateContext = botStateContext;
        this.trainTicketsService = trainTicketsService;
        this.stationCodeService = stationCodeService;
        this.sendTicketsInfoService = sendTicketsInfoService;
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
        String replyToUser = "";
        String usersAnswer = inputMsg.getText();
        int userId = inputMsg.getFrom().getId();
        long chatId = inputMsg.getChatId();

        TrainSearchRequestData requestData = userDataCache.getUserTrainSearchData(userId);

        BotState botState = botStateContext.getCurrentState();

        if (botState.equals(BotState.ASK_STATION_DEPART)) {
            replyToUser = "Введите станцию отправления";
            botStateContext.setCurrentState(BotState.ASK_STATION_ARRIVAL);
        }

        if (botState.equals(BotState.ASK_STATION_ARRIVAL)) {
            requestData.setDepartureStation(usersAnswer);
            userDataCache.saveTrainSearchData(userId, requestData);
            replyToUser = "Введите станцию назначения";
            botStateContext.setCurrentState(BotState.ASK_DATE_DEPART);
        }

        if (botState.equals(BotState.ASK_DATE_DEPART)) {
            requestData.setArrivalStation(usersAnswer);
            userDataCache.saveTrainSearchData(userId, requestData);
            replyToUser = "Введите дату отправления";
            botStateContext.setCurrentState(BotState.DATE_DEPART_RECEIVED);
        }

        if (botState.equals(BotState.DATE_DEPART_RECEIVED)) {
            Date dateDepart;
            try {
                dateDepart = new SimpleDateFormat("dd.MM.yyyy").parse(usersAnswer);
            } catch (ParseException e) {
                replyToUser = String.format("%sНеверный формат даты, " +
                        "повторите ввод в формате День.Месяц.Год\nНапример: 31.02.2020", Emojis.NOTIFICATION_MARK_FAILED);
                return new SendMessage(inputMsg.getChatId(), replyToUser);
            }
            requestData.setDateDepart(dateDepart);
            userDataCache.saveTrainSearchData(userId, requestData);
            botStateContext.setCurrentState(BotState.TRAINS_SEARCH_STARTED);
            replyToUser = String.format("%s %s%n", Emojis.SUCCESS_MARK, "Завершен поиск поездов по заданным критериям.");

            int stationDepartCode = stationCodeService.getStationCode(requestData.getDepartureStation());
            if (stationDepartCode == -1) {
                botStateContext.setCurrentState(BotState.SHOW_MAIN_MENU);
                return new SendMessage(chatId,
                        String.format("%s Не найдена станция '%s', повторите поиск заново.", Emojis.NOTIFICATION_MARK_FAILED, requestData.getDepartureStation()));
            }

            int stationArrivalCode = stationCodeService.getStationCode(requestData.getArrivalStation());
            if (stationArrivalCode == -1) {
                botStateContext.setCurrentState(BotState.SHOW_MAIN_MENU);
                return new SendMessage(chatId,
                        String.format("%s Не найдена станция '%s', повторите поиск заново.", Emojis.NOTIFICATION_MARK_FAILED, requestData.getArrivalStation()));
            }

            List<Train> trainList = trainTicketsService.getTrainTicketsList(chatId, stationDepartCode, stationArrivalCode, dateDepart);
            if (trainList.isEmpty()) {
                botStateContext.setCurrentState(BotState.SHOW_MAIN_MENU);
                return new SendMessage(chatId, String.format("%s Не найдено ни одного поезда !", Emojis.NOTIFICATION_MARK_FAILED));
            }

            botStateContext.setCurrentState(BotState.SHOW_MAIN_MENU);
            sendTicketsInfoService.sendTrainTicketsInfo(chatId, trainList);

        }
        return new SendMessage(chatId, replyToUser);
    }


}



