package ru.otus.rzdtelegrambot.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.otus.rzdtelegrambot.model.CarType;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * @author Sergei Viacheslaev
 */
@Service
public class CarTypeLocalizationService {
    private ResourceBundle resourceBundle;
    private String localeTag;

    private String ECONOMY_SITTING;
    private String ECONOMY_SLEEPING;
    private String FIRST_SLEEPING;
    private String SECOND_SLEEPING;


    public CarTypeLocalizationService(@Value("${localeTag}") String currentLocale) {
        this.localeTag = currentLocale;

        resourceBundle = ResourceBundle.getBundle("carTypesLocalized/carTypes", Locale.forLanguageTag(localeTag));

        ECONOMY_SITTING = resourceBundle.getString("ECONOMY_CLASS_SITTING");
        ECONOMY_SLEEPING = resourceBundle.getString("ECONOMY_CLASS_SLEEPING");
        FIRST_SLEEPING = resourceBundle.getString("FIRST_CLASS_SLEEPING");
        SECOND_SLEEPING = resourceBundle.getString("SECOND_CLASS_SLEEPING");
    }


    public String getLocalizedCarTypeName(CarType carType) {
        if (carType == null) throw new RuntimeException("CarType value is NULL !");

        String localizedCarType = "";
        switch (carType) {
            case ECONOMY_CLASS_SITTING:
                localizedCarType = ECONOMY_SITTING;
                break;
            case ECONOMY_CLASS_SLEEPING:
                localizedCarType = ECONOMY_SLEEPING;
                break;
            case FIRST_CLASS_SLEEPING:
                localizedCarType = FIRST_SLEEPING;
                break;
            case SECOND_CLASS_SLEEPING:
                localizedCarType = SECOND_SLEEPING;
                break;
        }

        return localizedCarType;
    }
}
