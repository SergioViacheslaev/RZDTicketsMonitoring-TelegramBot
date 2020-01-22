package ru.otus.rzdtelegrambot.botconfig;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultBotOptions;

/**
 * @author Sergei Viacheslaev
 */

@Component
@ConfigurationProperties(prefix = "telegrambot")
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RZDTelegramBotConfig {
    String webHookPath;
    String userName;
    String botToken;

    DefaultBotOptions.ProxyType proxyType;
    String proxyHost;
    int proxyPort;
}

