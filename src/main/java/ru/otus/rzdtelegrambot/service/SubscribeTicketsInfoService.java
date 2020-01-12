package ru.otus.rzdtelegrambot.service;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import ru.otus.rzdtelegrambot.model.UserTicketsSubscription;
import ru.otus.rzdtelegrambot.repository.UserTicketsSubscriptionMongoRepository;

import java.util.List;

/**
 * @author Sergei Viacheslaev
 */
@Service
public class SubscribeTicketsInfoService {

    private UserTicketsSubscriptionMongoRepository subscriptionsRepository;

    public SubscribeTicketsInfoService(UserTicketsSubscriptionMongoRepository repository) {
        this.subscriptionsRepository = repository;
    }

    public void saveUserSubscription(CallbackQuery usersQuery) {
        long chatId = usersQuery.getMessage().getChatId();
        String[] queryData = usersQuery.getData().split("\\|");
        String callbackMessage = usersQuery.getMessage().getText();

        String trainNumber = queryData[1];
        String dateDepart = queryData[2];


        String stationDepart = callbackMessage.substring(callbackMessage.lastIndexOf("Отправление:") + 13,
                callbackMessage.indexOf(",")).trim();
        String stationArrival = callbackMessage.substring(callbackMessage.lastIndexOf("Прибытие:") + 10,
                callbackMessage.lastIndexOf(",")).trim();


        UserTicketsSubscription usersSubscription = new UserTicketsSubscription(chatId, trainNumber, stationDepart, stationArrival, dateDepart);

        subscriptionsRepository.save(usersSubscription);


    }


    public List<UserTicketsSubscription> getUsersSubscriptions(long chatId) {
        return subscriptionsRepository.findByChatId(chatId);
    }


}
