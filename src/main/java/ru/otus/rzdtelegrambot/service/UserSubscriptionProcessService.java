package ru.otus.rzdtelegrambot.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.otus.rzdtelegrambot.botapi.RZDTelegramBot;
import ru.otus.rzdtelegrambot.model.Car;
import ru.otus.rzdtelegrambot.model.Train;
import ru.otus.rzdtelegrambot.model.UserTicketsSubscription;
import ru.otus.rzdtelegrambot.repository.UserTicketsSubscriptionMongoRepository;
import ru.otus.rzdtelegrambot.utils.Emojis;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Сервис уведомлений,
 * рассылка информации об изменении цен на билеты.
 * <p>
 * Работает с подписками пользователей.
 *
 * @author Sergei Viacheslaev
 */
@Slf4j
@Service
public class UserSubscriptionProcessService {
    private final String PRICE_UP_MESSAGE;
    private final String PRICE_DOWN_MESSAGE;
    private final String PRICE_CHANGES_MESSAGE;
    private final String PRICE_LAST_UPDATES_MESSAGE;
    private final String CARS_TICKETS_MESSAGE;

    private final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");
    private UserTicketsSubscriptionMongoRepository subscriptionsRepository;
    private TrainTicketsGetInfoService trainTicketsGetInfoService;
    private StationCodeService stationCodeService;
    private CarsProcessingService carsProcessingService;
    private RZDTelegramBot telegramBot;

    public UserSubscriptionProcessService(UserTicketsSubscriptionMongoRepository subscriptionsRepository,
                                          TrainTicketsGetInfoService trainTicketsGetInfoService,
                                          StationCodeService stationCodeService,
                                          CarsProcessingService carsProcessingService,
                                          ReplyMessagesService messagesService,
                                          @Lazy RZDTelegramBot telegramBot) {
        this.subscriptionsRepository = subscriptionsRepository;
        this.trainTicketsGetInfoService = trainTicketsGetInfoService;
        this.stationCodeService = stationCodeService;
        this.carsProcessingService = carsProcessingService;
        this.telegramBot = telegramBot;

        PRICE_UP_MESSAGE = messagesService.getReplyText("subscription.PriceUp");
        PRICE_DOWN_MESSAGE = messagesService.getReplyText("subscription.PriceDown");
        PRICE_CHANGES_MESSAGE = messagesService.getReplyText("subscription.trainTicketsPriceChanges");
        PRICE_LAST_UPDATES_MESSAGE = messagesService.getReplyText("subscription.lastTicketPrices");
        CARS_TICKETS_MESSAGE = messagesService.getReplyText("subscription.carsTicketsInfo");
    }


    /**
     * Периодически смотрит за обновлением цен
     * по всей базе подписок.
     */
    @Scheduled(fixedRateString = "${subscriptions.processPeriod}")
    public void processAllUsersSubscriptions() {
        log.info("Выполняю обработку подписок пользователей.");
        subscriptionsRepository.findAll().forEach(this::processSubscription);
        log.info("Завершил обработку подписок пользователей.");
    }

    /**
     * Получает актуальные данные по билетам для текущей подписки,
     * если цена изменилась сохраняет последнюю и уведомляет клиента.
     */
    private void processSubscription(UserTicketsSubscription subscription) {
        List<Train> actualTrains = getActualTrains(subscription.getChatId(), subscription.getStationDepart(),
                subscription.getStationArrival(), subscription.getDateDepart());

        actualTrains.forEach(actualTrain -> {

            if (actualTrain.getNumber().equals(subscription.getTrainNumber()) &&
                    actualTrain.getDateDepart().equals(subscription.getDateDepart())) {


                List<Car> actualCarsWithMinimumPrice = carsProcessingService.filterCarsWithMinimumPrice(actualTrain.getAvailableCars());

                Map<String, List<Car>> updatedCarsNotification = processCarsLists(subscription.getSubscribedCars(),
                        actualCarsWithMinimumPrice);

                if (!updatedCarsNotification.isEmpty()) {
                    String priceChangesMessage = updatedCarsNotification.keySet().iterator().next();
                    List<Car> actualCars = updatedCarsNotification.get(priceChangesMessage);

                    subscription.setSubscribedCars(actualCars);
                    subscriptionsRepository.save(subscription);
                    sendUserNotification(subscription.getChatId(), priceChangesMessage, subscription.getTrainNumber(),
                            subscription.getTrainName(), subscription.getDateDepart(), actualCars);
                }
            }
        });

    }

    private List<Train> getActualTrains(long chatId, String stationDepart, String stationArrival, String dateDeparture) {
        int stationDepartCode = stationCodeService.getStationCode(stationDepart);
        int stationArrivalCode = stationCodeService.getStationCode(stationArrival);
        Date dateDepart = parseDateDeparture(dateDeparture);

        return trainTicketsGetInfoService.getTrainTicketsList(chatId, stationDepartCode, stationArrivalCode, dateDepart);
    }

    /**
     * Возвращает Мапу: Строку-уведомление и список обновленных цен в вагонах подписки.
     * Если цены не менялись, вернет пустую мапу.
     */
    private Map<String, List<Car>> processCarsLists(List<Car> subscribedCars, List<Car> actualCars) {
        StringBuilder notificationMessage = new StringBuilder();

        for (Car subscribedCar : subscribedCars) {

            for (Car actualCar : actualCars) {
                if (actualCar.getCarType().equals(subscribedCar.getCarType())) {

                    if (actualCar.getMinimalPrice() > subscribedCar.getMinimalPrice()) {
                        notificationMessage.append(String.format(PRICE_UP_MESSAGE, Emojis.NOTIFICATION_PRICE_UP,
                                actualCar.getCarType(), subscribedCar.getMinimalPrice(), actualCar.getMinimalPrice()));
                        subscribedCar.setMinimalPrice(actualCar.getMinimalPrice());
                    } else if (actualCar.getMinimalPrice() < subscribedCar.getMinimalPrice()) {
                        notificationMessage.append(String.format(PRICE_DOWN_MESSAGE, Emojis.NOTIFICATION_PRICE_DOWN,
                                actualCar.getCarType(), subscribedCar.getMinimalPrice(), actualCar.getMinimalPrice()));
                        subscribedCar.setMinimalPrice(actualCar.getMinimalPrice());
                    }
                    subscribedCar.setFreeSeats(actualCar.getFreeSeats());
                }
            }
        }

        return notificationMessage.length() == 0 ? Collections.emptyMap() : Collections.singletonMap(notificationMessage.toString(), subscribedCars);
    }

    private void sendUserNotification(long chatId, String priceChangeMessage, String trainNumber, String trainName,
                                      String dateDepart, List<Car> updatedCars) {

        StringBuilder notificationMessage = new StringBuilder(String.format(PRICE_CHANGES_MESSAGE,
                Emojis.NOTIFICATION_BELL, trainNumber, trainName, dateDepart)).append(priceChangeMessage);

        notificationMessage.append(PRICE_LAST_UPDATES_MESSAGE);

        for (Car car : updatedCars) {
            notificationMessage.append(String.format(CARS_TICKETS_MESSAGE,
                    car.getCarType(), car.getFreeSeats(), car.getMinimalPrice()));
        }

        telegramBot.sendMessage(chatId, notificationMessage.toString());
    }


    private Date parseDateDeparture(String dateDeparture) {
        Date dateDepart = null;
        try {
            dateDepart = DATE_FORMAT.parse(dateDeparture);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateDepart;
    }


}
