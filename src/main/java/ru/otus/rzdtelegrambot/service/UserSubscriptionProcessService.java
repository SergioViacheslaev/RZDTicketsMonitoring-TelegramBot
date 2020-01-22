package ru.otus.rzdtelegrambot.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.otus.rzdtelegrambot.botapi.RZDTelegramBot;
import ru.otus.rzdtelegrambot.model.Car;
import ru.otus.rzdtelegrambot.model.Train;
import ru.otus.rzdtelegrambot.model.UserTicketsSubscription;
import ru.otus.rzdtelegrambot.utils.Emojis;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

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
    private final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");
    private UserTicketsSubscriptionService subscriptionService;
    private TrainTicketsGetInfoService trainTicketsGetInfoService;
    private StationCodeService stationCodeService;
    private CarsProcessingService carsProcessingService;
    private ReplyMessagesService messagesService;
    private RZDTelegramBot telegramBot;

    public UserSubscriptionProcessService(UserTicketsSubscriptionService subscriptionService,
                                          TrainTicketsGetInfoService trainTicketsGetInfoService,
                                          StationCodeService stationCodeService,
                                          CarsProcessingService carsProcessingService,
                                          ReplyMessagesService messagesService,
                                          @Lazy RZDTelegramBot telegramBot) {
        this.subscriptionService = subscriptionService;
        this.trainTicketsGetInfoService = trainTicketsGetInfoService;
        this.stationCodeService = stationCodeService;
        this.carsProcessingService = carsProcessingService;
        this.messagesService = messagesService;
        this.telegramBot = telegramBot;
    }


    /**
     * Периодически смотрит за обновлением цен
     * по всей базе подписок.
     */
    @Scheduled(fixedRateString = "${subscriptions.processPeriod}")
    public void processAllUsersSubscriptions() {
        log.info("Выполняю обработку подписок пользователей.");
        subscriptionService.getAllSubscriptions().forEach(this::processSubscription);
        log.info("Завершил обработку подписок пользователей.");
    }

    /**
     * Получает актуальные данные по билетам для текущей подписки,
     * если цена изменилась сохраняет последнюю и уведомляет клиента.
     */
    private void processSubscription(UserTicketsSubscription subscription) {
        List<Train> actualTrains = getActualTrains(subscription);

        if (isTrainHasDeparted(actualTrains, subscription)) {
            subscriptionService.deleteUserSubscription(subscription.getId());
            telegramBot.sendMessage(messagesService.getReplyMessage(subscription.getChatId(), "subscription.trainHasDeparted",
                    Emojis.NOTIFICATION_BELL, subscription.getTrainNumber(), subscription.getTrainName(),
                    subscription.getDateDepart(), subscription.getTimeDepart()));
            return;
        }

        actualTrains.forEach(actualTrain -> {

            if (actualTrain.getNumber().equals(subscription.getTrainNumber()) &&
                    actualTrain.getDateDepart().equals(subscription.getDateDepart())) {

                List<Car> actualCarsWithMinimumPrice = carsProcessingService.filterCarsWithMinimumPrice(actualTrain.getAvailableCars());

                Map<String, List<Car>> updatedCarsNotification = processCarsLists(subscription.getSubscribedCars(),
                        actualCarsWithMinimumPrice);

                if (!updatedCarsNotification.isEmpty()) {
                    String priceChangesMessage = updatedCarsNotification.keySet().iterator().next();
                    List<Car> updatedCars = updatedCarsNotification.get(priceChangesMessage);

                    subscription.setSubscribedCars(updatedCars);
                    subscriptionService.saveUserSubscription(subscription);
                    sendUserNotification(subscription, priceChangesMessage, updatedCars);
                }
            }
        });


    }

    private List<Train> getActualTrains(UserTicketsSubscription subscription) {
        int stationDepartCode = stationCodeService.getStationCode(subscription.getStationDepart());
        int stationArrivalCode = stationCodeService.getStationCode(subscription.getStationArrival());
        Date dateDeparture = parseDateDeparture(subscription.getDateDepart());

        return trainTicketsGetInfoService.getTrainTicketsList(subscription.getChatId(),
                stationDepartCode, stationArrivalCode, dateDeparture);
    }

    private boolean isTrainHasDeparted(List<Train> actualTrains, UserTicketsSubscription subscription) {
        return actualTrains.stream().map(Train::getNumber).noneMatch(Predicate.isEqual(subscription.getTrainNumber()));
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
                        notificationMessage.append(messagesService.getReplyText("subscription.PriceUp", Emojis.NOTIFICATION_PRICE_UP,
                                actualCar.getCarType(), subscribedCar.getMinimalPrice(), actualCar.getMinimalPrice()));
                        subscribedCar.setMinimalPrice(actualCar.getMinimalPrice());
                    } else if (actualCar.getMinimalPrice() < subscribedCar.getMinimalPrice()) {
                        notificationMessage.append(messagesService.getReplyText("subscription.PriceDown", Emojis.NOTIFICATION_PRICE_DOWN,
                                actualCar.getCarType(), subscribedCar.getMinimalPrice(), actualCar.getMinimalPrice()));
                        subscribedCar.setMinimalPrice(actualCar.getMinimalPrice());
                    }
                    subscribedCar.setFreeSeats(actualCar.getFreeSeats());
                }
            }
        }

        return notificationMessage.length() == 0 ? Collections.emptyMap() : Collections.singletonMap(notificationMessage.toString(), subscribedCars);
    }

    private void sendUserNotification(UserTicketsSubscription subscription, String priceChangeMessage, List<Car> updatedCars) {
        StringBuilder notificationMessage = new StringBuilder(messagesService.getReplyText("subscription.trainTicketsPriceChanges",
                Emojis.NOTIFICATION_BELL, subscription.getTrainNumber(), subscription.getTrainName(),
                subscription.getDateDepart(), subscription.getTimeDepart(), subscription.getStationArrival())).append(priceChangeMessage);

        notificationMessage.append(messagesService.getReplyText("subscription.lastTicketPrices"));

        for (Car car : updatedCars) {
            notificationMessage.append(messagesService.getReplyText("subscription.carsTicketsInfo",
                    car.getCarType(), car.getFreeSeats(), car.getMinimalPrice()));
        }

        telegramBot.sendMessage(subscription.getChatId(), notificationMessage.toString());
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
