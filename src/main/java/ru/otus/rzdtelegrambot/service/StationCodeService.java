package ru.otus.rzdtelegrambot.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.otus.rzdtelegrambot.model.TrainStation;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Sergei Viacheslaev
 */
@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StationCodeService {
    final String stationCodeRequestTemplate = "https://pass.rzd.ru/suggester?stationNamePart={stationNamePart}&lang=ru";
    final RestTemplate restTemplate = new RestTemplate();

    Map<String, Integer> stationCodeCache = new HashMap<>();

    public int getStationCode(String stationName) {
        String stationNameParam = stationName.toUpperCase();

        Integer stationCode = stationCodeCache.get(stationNameParam);
        if (stationCode != null) return stationCode;

        processStationCodeRequest(stationNameParam);

        stationCode = stationCodeCache.get(stationNameParam);

        if (stationCode != null) {
            return stationCode;
        } else {
            throw new RuntimeException("Станция не найдена ! stationCode= null");
        }
    }


    private void processStationCodeRequest(String stationNamePart) {

        ResponseEntity<TrainStation[]> response =
                restTemplate.getForEntity(
                        stationCodeRequestTemplate,
                        TrainStation[].class, stationNamePart);


        TrainStation[] stations = response.getBody();

        log.info("Stations {}", Arrays.toString(stations));

        for (TrainStation station : stations) {
            stationCodeCache.put(station.getStationName(), station.getStationCode());
        }


    }

}
