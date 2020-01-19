package ru.otus.rzdtelegrambot.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.otus.rzdtelegrambot.cache.StationsCache;
import ru.otus.rzdtelegrambot.model.TrainStation;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


/**
 * Поиск станции в справочнике станций
 *
 * @author Sergei Viacheslaev
 */
@Service
@Slf4j
public class StationBookService {
    @Value("${stationcodeservice.requesttemplate}")
    private String stationSearchTemplate;
    private RestTemplate restTemplate;
    private StationsCache stationsCache;
    private ReplyMessagesService messagesService;

    public StationBookService(RestTemplate restTemplate, StationsCache stationsCache, ReplyMessagesService messagesService) {
        this.restTemplate = restTemplate;
        this.stationsCache = stationsCache;
        this.messagesService = messagesService;
    }

    public SendMessage processStationNamePart(long chatId, String stationNamePartParam) {
        String searchedStationName = stationNamePartParam.toUpperCase();

        Optional<String> optionalStationName = stationsCache.getStationName(searchedStationName);
        if (optionalStationName.isPresent()) {
            return messagesService.getStationFoundMessage(chatId, "reply.stationBook.stationFound", optionalStationName.get());
        }

        List<TrainStation> trainStations = sendStationSearchRequest(searchedStationName);

        List<String> foundedStationNames = trainStations.stream().
                map(TrainStation::getStationName).filter(stationName -> stationName.contains(searchedStationName)).collect(Collectors.toList());

        if (foundedStationNames.isEmpty()) {
            return messagesService.getReplyMessage(chatId, "reply.stationBookMenu.stationNotFound");
        }

        return messagesService.getStationsFoundMessage(chatId, "reply.stationBook.stationsFound", foundedStationNames);

    }

    private List<TrainStation> sendStationSearchRequest(String stationNamePart) {
        ResponseEntity<TrainStation[]> response =
                restTemplate.getForEntity(
                        stationSearchTemplate,
                        TrainStation[].class, stationNamePart);
        TrainStation[] stations = response.getBody();
        if (stations == null) {
            return Collections.emptyList();
        }

        for (TrainStation station : stations) {
            stationsCache.addStationToCache(station.getStationName(), station.getStationCode());
        }

        return List.of(stations);
    }

}
