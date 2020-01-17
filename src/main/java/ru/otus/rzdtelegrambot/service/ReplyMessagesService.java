package ru.otus.rzdtelegrambot.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.otus.rzdtelegrambot.utils.Emojis;

import java.util.Locale;


/**
 * @author Sergei Viacheslaev
 */
@Service
public class ReplyMessagesService {

    private final Locale locale;

    private MessageSource messageSource;

    public ReplyMessagesService(@Value("${localeTag}") String localeTag, MessageSource messageSource) {
        this.messageSource = messageSource;
        this.locale = Locale.forLanguageTag(localeTag);
        System.out.println(locale);
        System.out.println(locale.getCountry());

    }


    public SendMessage getReplyMessage(long chatId, String replyMessage) {
        return new SendMessage(chatId, String.format("%s %s", Emojis.NOTIFICATION_MARK_FAILED, messageSource.getMessage(replyMessage, null, locale)));
    }
}
