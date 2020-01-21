package ru.otus.rzdtelegrambot.service;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.otus.rzdtelegrambot.cache.StationsDataCache;
import ru.otus.rzdtelegrambot.model.TrainStation;

import java.util.Optional;

/**
 * Позволяет получить код станции по ее названию.
 *
 * @author Sergei Viacheslaev
 */
@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
public class StationCodeService {
    @Value("${stationcodeservice.requesttemplate}")
    private String stationCodeRequestTemplate;
    private RestTemplate restTemplate;
    private StationsDataCache stationsCache;


    public StationCodeService(RestTemplate restTemplate, StationsDataCache stationsCache) {
        this.restTemplate = restTemplate;
        this.stationsCache = stationsCache;
    }

    public int getStationCode(String stationName) {
        String stationNameParam = stationName.toUpperCase();

        Optional<Integer> stationCodeOptional = stationsCache.getStationCode(stationNameParam);
        if (stationCodeOptional.isPresent()) return stationCodeOptional.get();

        if (processStationCodeRequest(stationNameParam).isEmpty()) {
            return -1;
        }

        return stationsCache.getStationCode(stationNameParam).orElse(-1);

    }

    private Optional<TrainStation[]> processStationCodeRequest(String stationNamePart) {
        ResponseEntity<TrainStation[]> response =
                restTemplate.getForEntity(
                        stationCodeRequestTemplate,
                        TrainStation[].class, stationNamePart);
        TrainStation[] stations = response.getBody();
        if (stations == null) {
            return Optional.empty();
        }

        for (TrainStation station : stations) {
            stationsCache.addStationToCache(station.getStationName(), station.getStationCode());
        }

        return Optional.of(stations);
    }
}
