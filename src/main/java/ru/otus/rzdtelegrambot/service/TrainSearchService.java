package ru.otus.rzdtelegrambot.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author Sergei Viacheslaev
 */

@Service
@Slf4j
public class TrainSearchService {
    public void createUsersSearchRequest() {
        log.info("Начинаю формировать объект поиска поездов....");
    }
}
