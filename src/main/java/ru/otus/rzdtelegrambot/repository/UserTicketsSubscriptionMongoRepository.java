package ru.otus.rzdtelegrambot.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import ru.otus.rzdtelegrambot.model.UserTicketsSubscription;

import java.util.List;

/**
 * @author Sergei Viacheslaev
 */
@Repository
public interface UserTicketsSubscriptionMongoRepository extends MongoRepository<UserTicketsSubscription, String> {
    List<UserTicketsSubscription> findByChatId(long chatId);

    List<UserTicketsSubscription> findByChatIdAndTrainNumberAndDateDepart(long chatId, String trainNumber, String dateDepart);
}
