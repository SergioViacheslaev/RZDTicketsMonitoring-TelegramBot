package ru.otus.rzdtelegrambot.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.otus.rzdtelegrambot.botapi.RZDTelegramBot;
import ru.otus.rzdtelegrambot.model.Train;
import ru.otus.rzdtelegrambot.utils.NotificationMessage;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Отправляет запросы к RZD API.
 * Получает данные об актуальных поездах.
 *
 * @author Sergei Viacheslaev
 */
@Slf4j
@Service
public class TrainTicketsInfoService {
    private final String TRAIN_INFO_RID_REQUEST = "https://pass.rzd.ru/timetable/public/ru?layer_id=5827&dir=0&tfl=3&" +
            "checkSeats=1&code0={STATION_DEPART_CODE}&dt0={DATE_DEPART}&code1={STATION_ARRIVAL_CODE}";
    private final String TRAIN_INFO_REQUEST_TEMPLATE = "https://pass.rzd.ru/timetable/public/ru?layer_id=5827&rid={RID_VALUE}";
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd.MM.yyyy");
    private final RestTemplate restTemplate;
    private RZDTelegramBot telegramBot;

    public TrainTicketsInfoService(RestTemplate restTemplate, @Lazy RZDTelegramBot telegramBot) {
        this.restTemplate = restTemplate;
        this.telegramBot = telegramBot;
    }

    public List<Train> getTrainTicketsList(long chatId, int stationDepartCode, int stationArrivalCode, Date dateDepart) {
        String dateDepartStr = dateFormatter.format(dateDepart);
        Map<String, String> urlParams = new HashMap<>();
        urlParams.put("STATION_DEPART_CODE", String.valueOf(stationDepartCode));
        urlParams.put("STATION_ARRIVAL_CODE", String.valueOf(stationArrivalCode));
        urlParams.put("DATE_DEPART", dateDepartStr);

        //1. Get RID and cookies
        HttpHeaders httpHeaders = null;
        String rid = "";

        ResponseEntity<String> passRzdResp
                = restTemplate.getForEntity(TRAIN_INFO_RID_REQUEST, String.class,
                urlParams);

        String jsonRespBody = passRzdResp.getBody();

        if (jsonRespBody == null) {
            telegramBot.sendMessage(chatId, NotificationMessage.TICKET_SEARCH_DATEDEPART_OUTDATE.toString());
            return Collections.emptyList();
        }

        if (jsonRespBody.contains("находится за пределами периода")) {
            telegramBot.sendMessage(chatId, NotificationMessage.TICKET_SEARCH_DATEDEPART_OUTDATE.toString());
            return Collections.emptyList();
        }

        try {
            JsonNode jsonNode = objectMapper.readTree(jsonRespBody);
            rid = jsonNode.get("RID").asText();
            httpHeaders = passRzdResp.getHeaders();
        } catch (JsonProcessingException e) {
            telegramBot.sendMessage(chatId, String.format("Ошибка получения данных от RZD, обратитесь к разработчику: %s", e.getMessage()));
            e.printStackTrace();
            return Collections.emptyList();
        }

        sleep(2000);

        List<String> cookies = httpHeaders.get("Set-Cookie");

        String jSessionId = cookies.get(cookies.size() - 1);
        jSessionId = jSessionId.substring(jSessionId.indexOf("=") + 1, jSessionId.indexOf(";"));

        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("Cookie", "lang=ru");
        requestHeaders.add("Cookie", "JSESSIONID=" + jSessionId);
        requestHeaders.add("Cookie", "AuthFlag=false");

        //2. Get JSON Trains Info
        HttpEntity<String> httpEntity = new HttpEntity<>(requestHeaders);
        ResponseEntity<String> resultResponse = restTemplate.exchange(TRAIN_INFO_REQUEST_TEMPLATE,
                HttpMethod.GET,
                httpEntity,
                String.class, rid);

        if (!isResponseResultOK(resultResponse)) {
            resultResponse = restTemplate.exchange(TRAIN_INFO_REQUEST_TEMPLATE,
                    HttpMethod.GET,
                    httpEntity,
                    String.class, rid);

        }

        return parseResponseBody(resultResponse.getBody());
    }

    private boolean isResponseResultOK(ResponseEntity<String> resultResponse) {
        if (resultResponse.getBody().contains("OK"))
            return true;

        log.error("Result of responce is RID - try again...");
        sleep(500);
        return false;
    }

    private List<Train> parseResponseBody(String responseBody) {
        List<Train> trainList = new ArrayList<>();
        try {
            JsonNode trainsNode = objectMapper.readTree(responseBody).path("tp").findPath("list");

            trainList = Arrays.asList(objectMapper.readValue(trainsNode.toString(), Train[].class));

            return trainList;

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return trainList;
    }

    private void sleep(int i) {
        try {
            Thread.sleep(i);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
