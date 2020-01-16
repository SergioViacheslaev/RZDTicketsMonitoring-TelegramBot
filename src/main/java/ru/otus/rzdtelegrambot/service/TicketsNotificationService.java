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
public class TicketsNotificationService {
    private final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");
    private UserTicketsSubscriptionMongoRepository subscriptionsRepository;
    private TrainTicketsInfoService trainTicketsInfoService;
    private StationCodeService stationCodeService;
    private CarsProcessingService carsProcessingService;
    private RZDTelegramBot telegramBot;

    public TicketsNotificationService(UserTicketsSubscriptionMongoRepository subscriptionsRepository,
                                      TrainTicketsInfoService trainTicketsInfoService,
                                      StationCodeService stationCodeService,
                                      CarsProcessingService carsProcessingService,
                                      @Lazy RZDTelegramBot telegramBot) {
        this.subscriptionsRepository = subscriptionsRepository;
        this.trainTicketsInfoService = trainTicketsInfoService;
        this.stationCodeService = stationCodeService;
        this.carsProcessingService = carsProcessingService;
        this.telegramBot = telegramBot;
    }


    /**
     * Периодически смотрит за обновлением цен
     * по всей базе подписок.
     */
    @Scheduled(fixedRateString = "${fixed-rate.in.milliseconds}")
    public void reportCurrentTime() {
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

        sleep(250);

    }

    private List<Train> getActualTrains(long chatId, String stationDepart, String stationArrival, String dateDeparture) {
        int stationDepartCode = stationCodeService.getStationCode(stationDepart);
        int stationArrivalCode = stationCodeService.getStationCode(stationArrival);
        Date dateDepart = parseDateDeparture(dateDeparture);

        return trainTicketsInfoService.getTrainTicketsList(chatId, stationDepartCode, stationArrivalCode, dateDepart);
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
                        notificationMessage.append(String.format("%sВозросла цена на вагоны %s, было %s ₽.%n", Emojis.NOTIFICATION_PRICE_UP,
                                actualCar.getCarType(), subscribedCar.getMinimalPrice()));
                        subscribedCar.setMinimalPrice(actualCar.getMinimalPrice());
                    } else if (actualCar.getMinimalPrice() < subscribedCar.getMinimalPrice()) {
                        notificationMessage.append(String.format("%sПонизилась цена на вагоны %s, было %s ₽.%n", Emojis.NOTIFICATION_PRICE_DOWN,
                                actualCar.getCarType(), subscribedCar.getMinimalPrice()));
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

        StringBuilder notificationMessage = new StringBuilder(String.format("%s Изменились цены на поезд №%s '%s', отправлением %s.%n%n",
                Emojis.NOTIFICATION_BELL, trainNumber, trainName, dateDepart)).append(priceChangeMessage);

        notificationMessage.append("Последние данные по билетам:\n");

        for (Car car : updatedCars) {
            notificationMessage.append(String.format("%s: свободных мест %s от %d ₽.%n",
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

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
