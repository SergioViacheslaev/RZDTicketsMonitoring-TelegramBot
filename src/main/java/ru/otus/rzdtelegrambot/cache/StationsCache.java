package ru.otus.rzdtelegrambot.cache;

import java.util.Optional;

/**
 * @author Sergei Viacheslaev
 */
public interface StationsCache {
    Optional<String> getStationName(String stationNameParam);

    Optional<Integer> getStationCode(String stationNameParam);

    void addStationToCache(String stationName, int stationCode);
}
