package ru.otus.rzdtelegrambot.botconfig;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.telegram.telegrambots.bots.DefaultBotOptions;


/**
 * @author Sergei Viacheslaev
 */

@ConfigurationProperties(prefix = "telegrambot")
@Getter
@Setter
public class RZDTelegramBotConfig {

    private String userName;
    private String token;

    private String proxyHost;
    private int proxyPort;
    private DefaultBotOptions.ProxyType proxyType;

}
