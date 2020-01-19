package ru.otus.rzdtelegrambot.cache;

import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author Sergei Viacheslaev
 */
@Component
@Getter
public class StationsCache {
    private Map<String, Integer> stationCodeCache = new HashMap<>();

    public Optional<String> getStationName(String stationNameParam) {
        return stationCodeCache.keySet().stream().filter(stationName -> stationName.equals(stationNameParam)).findFirst();
    }

    public Optional<Integer> getStationCode(String stationNameParam) {
        return Optional.ofNullable(stationCodeCache.get(stationNameParam));
    }

    public void addStationToCache(String stationName, int stationCode) {
        stationCodeCache.put(stationName,stationCode);
    }

}
