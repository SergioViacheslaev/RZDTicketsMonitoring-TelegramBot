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
import ru.otus.rzdtelegrambot.model.TrainStation;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
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
    private Map<String, Integer> stationCodeCache = new HashMap<>();

    public StationCodeService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public int getStationCode(String stationName) {
        String stationNameParam = stationName.toUpperCase();

        Integer stationCode = stationCodeCache.get(stationNameParam);
        if (stationCode != null) return stationCode;

        if (processStationCodeRequest(stationNameParam).isEmpty()) {
            return -1;
        }

        stationCode = stationCodeCache.get(stationNameParam);

        if (stationCode != null) {
            return stationCode;
        } else {
            return -1;
        }
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

        log.info("Stations {}", Arrays.toString(stations));
        for (TrainStation station : stations) {
            stationCodeCache.put(station.getStationName(), station.getStationCode());
        }

        return Optional.of(stations);
    }
}
