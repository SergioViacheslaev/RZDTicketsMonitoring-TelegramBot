package ru.otus.rzdtelegrambot.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.otus.rzdtelegrambot.utils.Emojis;

import java.util.List;
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
    }

    public SendMessage getSuccessReplyMessage(long chatId, String replyMessage) {
        return new SendMessage(chatId, String.format("%s %s", Emojis.SUCCESS_MARK, messageSource.getMessage(replyMessage, null, locale)));
    }

    public SendMessage getReplyMessage(long chatId, String replyMessage) {
        return new SendMessage(chatId, messageSource.getMessage(replyMessage, null, locale));
    }

    public SendMessage getTrainSubscribedMessage(long chatId, String trainNumber, String dateDepart) {
        return new SendMessage(chatId, String.format(messageSource.getMessage("reply.query.train.subscribed", null, locale),
                trainNumber, dateDepart));
    }

    public SendMessage getTrainUnsubscribedMessage(long chatId, String trainNumber, String dateDepart) {
        return new SendMessage(chatId, String.format(messageSource.getMessage("reply.query.train.unsubscribed", null, locale),
                trainNumber, dateDepart));
    }

    public SendMessage getTrainSearchFinishedOKMessage(long chatId, String replyMessage) {
        return new SendMessage(chatId, String.format("%s %s", Emojis.SUCCESS_MARK, messageSource.getMessage(replyMessage, null, locale)));
    }


    public SendMessage getWarningReplyMessage(long chatId, String replyMessage) {
        return new SendMessage(chatId, String.format("%s %s", Emojis.NOTIFICATION_MARK_FAILED, messageSource.getMessage(replyMessage, null, locale)));
    }

    public SendMessage getStationFoundMessage(long chatId, String replyMessage, String stationName) {
        return new SendMessage(chatId, String.format("%s %s %s", Emojis.SUCCESS_MARK,
                messageSource.getMessage(replyMessage, null, locale), stationName));
    }

    public SendMessage getStationsFoundMessage(long chatId, String replyMessage, List<String> foundedStationNames) {
        StringBuilder stationsNamesMessage = new StringBuilder();
        foundedStationNames.forEach(foundedName -> stationsNamesMessage.append(String.format("%s%n", foundedName)));

        return new SendMessage(chatId, String.format("%s %s%n%s", Emojis.SUCCESS_MARK,
                messageSource.getMessage(replyMessage, null, locale), stationsNamesMessage.toString()));
    }
}
