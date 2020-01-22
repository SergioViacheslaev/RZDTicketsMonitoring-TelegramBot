package ru.otus.rzdtelegrambot.service;

import org.springframework.stereotype.Service;
import ru.otus.rzdtelegrambot.model.UserTicketsSubscription;
import ru.otus.rzdtelegrambot.repository.UserTicketsSubscriptionMongoRepository;

import java.util.List;
import java.util.Optional;

/**
 * Сохраняет, удаляет, ищет подписки пользователя.
 *
 * @author Sergei Viacheslaev
 */
@Service
public class UserTicketsSubscriptionService {

    private UserTicketsSubscriptionMongoRepository subscriptionsRepository;

    public UserTicketsSubscriptionService(UserTicketsSubscriptionMongoRepository repository) {
        this.subscriptionsRepository = repository;
    }

    public List<UserTicketsSubscription> getAllSubscriptions() {
        return subscriptionsRepository.findAll();
    }

    public void saveUserSubscription(UserTicketsSubscription usersSubscription) {
        subscriptionsRepository.save(usersSubscription);
    }

    public void deleteUserSubscription(String subscriptionID) {
        subscriptionsRepository.deleteById(subscriptionID);
    }


    public boolean hasTicketsSubscription(UserTicketsSubscription userSubscription) {
        return subscriptionsRepository.findByChatIdAndTrainNumberAndDateDepart(userSubscription.getChatId(),
                userSubscription.getTrainNumber(), userSubscription.getDateDepart()).size() > 0;
    }

    public Optional<UserTicketsSubscription> getUsersSubscriptionById(String subscriptionID) {
        return subscriptionsRepository.findById(subscriptionID);
    }

    public List<UserTicketsSubscription> getUsersSubscriptions(long chatId) {
        return subscriptionsRepository.findByChatId(chatId);
    }


}
