package ru.otus.rzdtelegrambot.botapi.handlers.callbackquery;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import ru.otus.rzdtelegrambot.botapi.BotStateContext;
import ru.otus.rzdtelegrambot.cache.UserDataCache;
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
    private BotStateContext botStateContext;
    private UserDataCache userDataCache;
    private SubscribeTicketsInfoQueryHandler subscribeHandler;
    private UnsubscribeTicketsInfoQueryHandler unsubscribeHandler;

    public CallbackQueryFacade(SubscribeTicketsInfoQueryHandler subscribeHandler,
                               UnsubscribeTicketsInfoQueryHandler unsubscribeHandler,
                               UserDataCache userDataCache,
                               BotStateContext botStateContext) {
        this.subscribeHandler = subscribeHandler;
        this.unsubscribeHandler = unsubscribeHandler;
        this.userDataCache = userDataCache;
        this.botStateContext = botStateContext;
    }

    public SendMessage processCallbackQuery(CallbackQuery callbackQuery) {
        CallbackQueryType queryType = CallbackQueryType.valueOf(callbackQuery.getData().split("\\|")[0]);

        SendMessage queryReply;

        switch (queryType) {
            case SUBSCRIBE:
                queryReply = subscribeHandler.handleCallbackQuery(callbackQuery);
                break;
            case UNSUBSCRIBE:
                queryReply = unsubscribeHandler.handleCallbackQuery(callbackQuery);
                break;
            case QUERY_PROCESSED:
                queryReply = new SendMessage(callbackQuery.getMessage().getChatId(), NotificationMessage.QUERY_WAS_PROCESSED.toString());
                break;
            default:
                queryReply = new SendMessage(callbackQuery.getMessage().getChatId(), NotificationMessage.HANDLE_KEYBOARD_QUERY_FAILED.toString());
                break;
        }

        userDataCache.saveUserBotState(callbackQuery.getFrom().getId(), botStateContext.getCurrentState());

        return queryReply;
    }


}
