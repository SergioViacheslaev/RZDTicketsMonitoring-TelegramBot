package ru.otus.rzdtelegrambot.service;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import ru.otus.rzdtelegrambot.model.Car;
import ru.otus.rzdtelegrambot.utils.CarPatterns;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Выполняет служебную обработку коллекций "Поездов"
 * Используется в нескольких классах.
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

    public List<Car> parseCarsFromMessage(String message) {
        List<Car> availableCars = new ArrayList<>();

        if (message.contains("Плац")) {
            String plackartTariff = message.substring(lastIndexOf(CarPatterns.PLACKART_START, message), lastIndexOf(CarPatterns.PLACKART_END, message));
            availableCars.add(new Car("Плац", 0, Integer.parseInt(plackartTariff)));
        }

        if (message.contains("Купе")) {
            String kuperTariff = message.substring(lastIndexOf(CarPatterns.KUPE_START, message), lastIndexOf(CarPatterns.KUPE_END, message));
            availableCars.add(new Car("Купе", 0, Integer.parseInt(kuperTariff)));
        }

        if (message.contains("Люкс")) {
            String luxTariff = message.substring(lastIndexOf(CarPatterns.LUX_START, message), lastIndexOf(CarPatterns.LUX_END, message));
            availableCars.add(new Car("Люкс", 0, Integer.parseInt(luxTariff)));
        }

        if (message.contains("Мягкий")) {
            String miagkiyTariff = message.substring(lastIndexOf(CarPatterns.MIAGKIY_START, message), lastIndexOf(CarPatterns.MIAGKIY_END, message));
            availableCars.add(new Car("Мягкий", 0, Integer.parseInt(miagkiyTariff)));
        }

        if (message.contains("Сид")) {
            String sidyachiTariff = message.substring(lastIndexOf(CarPatterns.SIDYACHI_START, message), lastIndexOf(CarPatterns.SIDYACHI_END, message));
            availableCars.add(new Car("Сид", 0, Integer.parseInt(sidyachiTariff)));
        }


        return availableCars;

    }


    public String parseTrainNumber(CallbackQuery callbackQuery) {

        return callbackQuery.getData().split("\\|")[1];
    }

    public String parseDateDepart(CallbackQuery callbackQuery) {
        return callbackQuery.getData().split("\\|")[2];
    }

    public String parseSubscriptionID(CallbackQuery callbackQuery) {
        return callbackQuery.getData().split("\\|")[2];
    }

    private int lastIndexOf(Pattern pattern, String input) {
        Matcher matcher = pattern.matcher(input);
        return matcher.find() ? matcher.end() : -1;
    }


}
