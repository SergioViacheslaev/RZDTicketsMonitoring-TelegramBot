package ru.otus.rzdtelegrambot.botapi.handlers;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import ru.otus.rzdtelegrambot.utils.CallbackQueryType;
import ru.otus.rzdtelegrambot.utils.NotificationMessage;

/**
 * Разбирает входящие запросы от кнопок клаватуры,
 * направляет нужному обработчику в зависимости от типа запроса.
 *
 * @author Sergei Viacheslaev
 */
@Component
public class CallbackQueryFacade {
    private SubscribeTicketsInfoQueryHandler subscribeHandler;
    private UnsubscribeTicketsInfoQueryHandler unsubscribeHandler;

    public CallbackQueryFacade(SubscribeTicketsInfoQueryHandler subscribeHandler, UnsubscribeTicketsInfoQueryHandler unsubscribeHandler) {
        this.subscribeHandler = subscribeHandler;
        this.unsubscribeHandler = unsubscribeHandler;
    }

    public SendMessage processCallbackQuery(CallbackQuery callbackQuery) {
        CallbackQueryType queryType = CallbackQueryType.valueOf(callbackQuery.getData().split("\\|")[0]);

        switch (queryType) {
            case SUBSCRIBE:
                return subscribeHandler.handleCallbackQuery(callbackQuery);
            case UNSUBSCRIBE:
                return unsubscribeHandler.handleCallbackQuery(callbackQuery);
            case QUERY_PROCESSED:
                return new SendMessage(callbackQuery.getMessage().getChatId(), NotificationMessage.QUERY_WAS_PROCESSED.toString());
            default:
                return new SendMessage(callbackQuery.getMessage().getChatId(), NotificationMessage.HANDLE_KEYBOARD_QUERY_FAILED.toString());
        }
    }


}
