package ru.otus.rzdtelegrambot.botconfig;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.ApiContext;
import ru.otus.rzdtelegrambot.botapi.RZDTelegramBot;

/**
 * @author Sergei Viacheslaev
 */

@Configuration
@PropertySource("classpath:telegrambot.properties")
@ConfigurationProperties(prefix = "telegrambot")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RZDTelegramBotConfig {
    String webHookPath;
    String userName;
    String botToken;

    //Временные прокси настройки для тестов
    DefaultBotOptions.ProxyType proxyType;
    String proxyHost;
    int proxyPort;


    @Bean
    public RZDTelegramBot RZDTelegramBot() {
        DefaultBotOptions options = ApiContext
                .getInstance(DefaultBotOptions.class);
        options.setProxyType(proxyType);
        options.setProxyHost(proxyHost);
        options.setProxyPort(proxyPort);

        RZDTelegramBot rzdTelegramBot = new RZDTelegramBot(options);
        rzdTelegramBot.setBotUsername(userName);
        rzdTelegramBot.setBotToken(botToken);
        rzdTelegramBot.setBotPath(webHookPath);


        return rzdTelegramBot;
    }

}

