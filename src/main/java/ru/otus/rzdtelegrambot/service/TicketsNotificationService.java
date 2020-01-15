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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
    private CarsProccessingService carsProccessingService;
    private RZDTelegramBot telegramBot;

    public TicketsNotificationService(UserTicketsSubscriptionMongoRepository subscriptionsRepository,
                                      TrainTicketsInfoService trainTicketsInfoService,
                                      StationCodeService stationCodeService,
                                      CarsProccessingService carsProccessingService,
                                      @Lazy RZDTelegramBot telegramBot) {
        this.subscriptionsRepository = subscriptionsRepository;
        this.trainTicketsInfoService = trainTicketsInfoService;
        this.stationCodeService = stationCodeService;
        this.carsProccessingService = carsProccessingService;
        this.telegramBot = telegramBot;
    }


    /**
     * Периодически смотрит за обновленицем цен
     * по всей базе подписок.
     */
    @Scheduled(fixedRateString = "${fixed-rate.in.milliseconds}")
    public void reportCurrentTime() {
        subscriptionsRepository.findAll().forEach(this::processSubscription);
    }

    /**
     * Получает актуальные данные по билетам для текущей подписки,
     * если цена изменилась сохраняет последнюю и уведомляет клиента.
     */
    private void processSubscription(UserTicketsSubscription subscription) {
        List<Train> actualTrains = getActualTrains(subscription.getStationDepart(), subscription.getStationArrival(), subscription.getDateDepart());

        actualTrains.forEach(actualTrain -> {

            if (actualTrain.getNumber().equals(subscription.getTrainNumber()) && actualTrain.getDateDepart().equals(subscription.getDateDepart())) {
                List<Car> updatedCars = processCarsLists(subscription.getChatId(), subscription.getSubscribedCars(), actualTrain.getAvailableCars());
                if (!updatedCars.isEmpty()) {
                    subscription.setSubscribedCars(updatedCars);
                    subscriptionsRepository.save(subscription);
                    sendUserNotification(subscription.getChatId(), subscription.getTrainNumber(),
                            subscription.getTrainName(), subscription.getDateDepart(), updatedCars);
                }
            }
        });

        sleep(250);

    }

    private List<Train> getActualTrains(String stationDepart, String stationArrival, String dateDeparture) {
        int stationDepartCode = stationCodeService.getStationCode(stationDepart);
        int stationArrivalCode = stationCodeService.getStationCode(stationArrival);
        Date dateDepart = parseDateDeparture(dateDeparture);

        return trainTicketsInfoService.getTrainTicketsList(stationDepartCode, stationArrivalCode, dateDepart);
    }

    private List<Car> processCarsLists(Long chatId, List<Car> subscribedCars, List<Car> actualCars) {
        List<Car> updatedCarsList = new ArrayList<>();
        StringBuilder notificationMessage = new StringBuilder();

        for (Car subscribedCar : subscribedCars) {

            for (Car actualCar : actualCars) {
                if (actualCar.getCarType().equals(subscribedCar.getCarType())) {
                    if (actualCar.getMinimalPrice() > subscribedCar.getMinimalPrice()) {
                        notificationMessage.append(String.format("%sВозросла цена на вагоны %s, была %s ₽.%n", Emojis.NOTIFICATION_PRICE_UP,
                                actualCar.getCarType(), subscribedCar.getMinimalPrice()));
                        updatedCarsList.add(actualCar);
                    } else if (actualCar.getMinimalPrice() < subscribedCar.getMinimalPrice()) {
                        notificationMessage.append(String.format("%sПонизилась цена на вагоны %s, была %s ₽.%n", Emojis.NOTIFICATION_PRICE_DOWN,
                                actualCar.getCarType(), subscribedCar.getMinimalPrice()));
                        updatedCarsList.add(actualCar);
                    }

                }
            }
        }

        //Если были изменения по ценам, то отправляем итоговое уведомление
        if (notificationMessage.length() != 0) {
            telegramBot.sendMessage(chatId, notificationMessage.toString());
        }

        return updatedCarsList.isEmpty() ? updatedCarsList : carsProccessingService.getCarsWithMinimumPrice(updatedCarsList);
    }

    private void sendUserNotification(long chatId, String trainNumber, String trainName, String dateDepart, List<Car> updatedCars) {
        StringBuilder carsInfo = new StringBuilder();
        for (Car car : updatedCars) {
            carsInfo.append(String.format("%s: свободных мест %s от %d ₽.%n",
                    car.getCarType(), car.getFreeSeats(), car.getMinimalPrice()));
        }

        telegramBot.sendMessage(chatId, String.format("%sАктуальные цены на поезд %s %s отправлением %s:%n%s", Emojis.NOTIFICATION_BELL,
                trainNumber, trainName, dateDepart, carsInfo));
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
