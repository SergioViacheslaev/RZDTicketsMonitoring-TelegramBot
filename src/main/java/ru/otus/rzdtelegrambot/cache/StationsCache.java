package ru.otus.rzdtelegrambot.cache;

import java.util.Optional;

public interface StationsCache {
    Optional<String> getStationName(String stationNameParam);

    Optional<Integer> getStationCode(String stationNameParam);

    void addStationToCache(String stationName, int stationCode);
}
