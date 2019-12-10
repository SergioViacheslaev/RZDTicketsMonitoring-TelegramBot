package ru.otus.rzdtelegrambot.sheduledtasks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.otus.rzdtelegrambot.botapi.RZDTelegramBot;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Sergei Viacheslaev
 */
@EnableAsync
@Component
public class TimeInfoTask {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
    private final Logger logger = LoggerFactory.getLogger(TimeInfoTask.class);

    private RZDTelegramBot rzdTelegramBot;

    public TimeInfoTask(RZDTelegramBot rzdTelegramBot) {
        this.rzdTelegramBot = rzdTelegramBot;
    }

    @Async
    @Scheduled(fixedRate = 5000)
    public void reportCurrentTime() {
        logger.info("The time is now {}", dateFormat.format(new Date()));
    }

 /*   @PostConstruct
    public void post() {
        reportCurrentTime();
    }*/
}
