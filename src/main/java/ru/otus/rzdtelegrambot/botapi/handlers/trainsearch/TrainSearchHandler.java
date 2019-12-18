package ru.otus.rzdtelegrambot.botapi.handlers.trainsearch;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.otus.rzdtelegrambot.botapi.BotState;
import ru.otus.rzdtelegrambot.botapi.BotStateContext;
import ru.otus.rzdtelegrambot.botapi.handlers.InputMessageHandler;
import ru.otus.rzdtelegrambot.repository.UserDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * @author Sergei Viacheslaev
 */

@Slf4j
@Component
public class TrainSearchHandler implements InputMessageHandler {

    private UserDatabase userDb;

    private BotStateContext botStateContext;

    public TrainSearchHandler(UserDatabase userDb, @Lazy BotStateContext botStateContext) {
        this.userDb = userDb;
        this.botStateContext = botStateContext;
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
        TrainSearchRequestData requestData = userDb.getUserTrainSearchData(userId);

        BotState botState = botStateContext.getCurrentState();

        if (botState.equals(BotState.ASK_STATION_DEPART)) {
            replyToUser = "Введите станцию отправления";
            botStateContext.setCurrentState(BotState.ASK_STATION_ARRIVAL);
        }

        if (botState.equals(BotState.ASK_STATION_ARRIVAL)) {
            requestData.setDepartureStation(usersAnswer);
            userDb.saveTrainSearchData(userId, requestData);
            replyToUser = "Введите станцию назначения";
            botStateContext.setCurrentState(BotState.ASK_DATE_DEPART);
        }

        if (botState.equals(BotState.ASK_DATE_DEPART)) {
            requestData.setArrivalStation(usersAnswer);
            userDb.saveTrainSearchData(userId, requestData);
            replyToUser = "Введите дату отправления";
            botStateContext.setCurrentState(BotState.DATE_DEPART_RECEIVED);
        }

        //todo: Добавить проверку корректности диапазона времени
        if (botState.equals(BotState.DATE_DEPART_RECEIVED)) {
            Date dateDepart;
            try {
                dateDepart = new SimpleDateFormat("dd.MM.yyyy").parse(usersAnswer);
            } catch (ParseException e) {
                log.error("Неверный формат даты {}", e.getMessage());
                replyToUser = "Неверный формат даты, " +
                        "повторите ввод в формате День.Месяц.Год\nНапример: 31.02.2020";
                return new SendMessage(inputMsg.getChatId(), replyToUser);
            }
            requestData.setDateDepart(dateDepart);
            userDb.saveTrainSearchData(userId, requestData);
            botStateContext.setCurrentState(BotState.TRAINS_SEARCH_STARTED);
            replyToUser = "Начинаю поиск поездов по заданным критериям: \n" + requestData;

        }
        return new SendMessage(inputMsg.getChatId(), replyToUser);
    }


}



