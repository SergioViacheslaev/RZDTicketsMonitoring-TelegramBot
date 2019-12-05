package ru.otus.rzdtelegrambot.botconfig;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;


/**
 * @author Sergei Viacheslaev
 */

@ConfigurationProperties(prefix = "telegrambot")
@Getter
public class RZDTelegramBotConfig {

    private String proxyHost;
    private int proxyPort;


    public void setProxyHost(String proxyHost) {
        this.proxyHost = proxyHost;
    }

    public void setProxyPort(int proxyPort) {
        this.proxyPort = proxyPort;
    }


}
