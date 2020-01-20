package ru.otus.rzdtelegrambot.service;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import ru.otus.rzdtelegrambot.model.Car;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Выполняет служебную обработку коллекций "Поездов".
 * Парсит сообщений входящих запросов от клавиатуры.
 *
 * @author Sergei Viacheslaev
 */
@Service
public class CarsProcessingService {


    public List<Car> filterCarsWithMinimumPrice(List<Car> cars) {
        return new ArrayList<>(cars.stream()
                .collect(Collectors.toMap(Car::getCarType, Function.identity(),
                        BinaryOperator.minBy(Comparator.comparing(Car::getMinimalPrice)))).values());
    }

    public String parseTrainNumberFromSubscribeQuery(CallbackQuery callbackQuery) {

        return callbackQuery.getData().split("\\|")[1];
    }

    public String parseDateDepartFromSubscribeQuery(CallbackQuery callbackQuery) {
        return callbackQuery.getData().split("\\|")[2];
    }

    public String parseSubscriptionIdFromUnsubscribeQuery(CallbackQuery callbackQuery) {
        return callbackQuery.getData().split("\\|")[1];
    }

}
