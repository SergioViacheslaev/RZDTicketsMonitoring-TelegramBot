package ru.otus.rzdtelegrambot.appconfig;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.ApiContext;
import ru.otus.rzdtelegrambot.botapi.RZDTelegramBot;
import ru.otus.rzdtelegrambot.botapi.TelegramFacade;
import ru.otus.rzdtelegrambot.botconfig.RZDTelegramBotConfig;


@Configuration
public class AppConfig {
    private RZDTelegramBotConfig botConfig;

    public AppConfig(RZDTelegramBotConfig rzdTelegramBotConfig) {
        this.botConfig = rzdTelegramBotConfig;
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource
                = new ReloadableResourceBundleMessageSource();

        messageSource.setBasename("classpath:messages");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }

    @Bean
    public RZDTelegramBot RZDTelegramBot(TelegramFacade telegramFacade) {
        DefaultBotOptions options = ApiContext
                .getInstance(DefaultBotOptions.class);

        options.setProxyHost(botConfig.getProxyHost());
        options.setProxyPort(botConfig.getProxyPort());
        options.setProxyType(botConfig.getProxyType());

        RZDTelegramBot rzdTelegramBot = new RZDTelegramBot(options, telegramFacade);
        rzdTelegramBot.setBotUsername(botConfig.getUserName());
        rzdTelegramBot.setBotToken(botConfig.getBotToken());
        rzdTelegramBot.setBotPath(botConfig.getWebHookPath());

        return rzdTelegramBot;
    }
}
