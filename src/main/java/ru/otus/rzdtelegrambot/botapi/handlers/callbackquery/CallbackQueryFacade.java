package ru.otus.rzdtelegrambot.botapi.handlers.callbackquery;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import ru.otus.rzdtelegrambot.service.ReplyMessagesService;

import java.util.List;
import java.util.Optional;

/**
 * Разбирает входящие запросы от кнопок клаватуры,
 * находит нужный обработчик по типу запроса.
 *
 * @author Sergei Viacheslaev
 */
@Component
public class CallbackQueryFacade {
    private ReplyMessagesService messagesService;
    private List<CallbackQueryHandler> callbackQueryHandlers;

    public CallbackQueryFacade(ReplyMessagesService messagesService,
                               List<CallbackQueryHandler> callbackQueryHandlers) {
        this.messagesService = messagesService;
        this.callbackQueryHandlers = callbackQueryHandlers;
    }

    public SendMessage processCallbackQuery(CallbackQuery usersQuery) {
        CallbackQueryType usersQueryType = CallbackQueryType.valueOf(usersQuery.getData().split("\\|")[0]);

        SendMessage userReply;

        Optional<CallbackQueryHandler> queryHandler = callbackQueryHandlers.stream().
                filter(callbackQuery -> callbackQuery.getHandlerQueryType().equals(usersQueryType)).findFirst();

        if (queryHandler.isPresent()) {
            userReply = queryHandler.get().handleCallbackQuery(usersQuery);
        } else {
            userReply = messagesService.getWarningReplyMessage(usersQuery.getMessage().getChatId(), "reply.query.failed");
        }

        return userReply;
    }
}
