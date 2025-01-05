package ru.otus.rzdtelegrambot.botapi.handlers.callbackquery;

import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class CallbackQueryFacade {
    private final ReplyMessagesService messagesService;
    private final List<CallbackQueryHandler> callbackQueryHandlers;

    public SendMessage processCallbackQuery(CallbackQuery usersQuery) {
        CallbackQueryType usersQueryType = CallbackQueryType.valueOf(usersQuery.getData().split("\\|")[0]);

        Optional<CallbackQueryHandler> queryHandler = callbackQueryHandlers.stream().
                                                                           filter(callbackQuery -> callbackQuery.getHandlerQueryType().equals(usersQueryType)).findFirst();

        return queryHandler.map(handler -> handler.handleCallbackQuery(usersQuery)).
                           orElse(messagesService.getWarningReplyMessage(usersQuery.getMessage().getChatId(), "reply.query.failed"));
    }
}
