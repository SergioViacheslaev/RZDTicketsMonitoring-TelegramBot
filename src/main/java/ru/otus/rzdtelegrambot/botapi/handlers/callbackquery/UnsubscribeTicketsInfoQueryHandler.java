package ru.otus.rzdtelegrambot.botapi.handlers.callbackquery;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import ru.otus.rzdtelegrambot.botapi.RZDTelegramBot;
import ru.otus.rzdtelegrambot.model.UserTicketsSubscription;
import ru.otus.rzdtelegrambot.service.ParseQueryDataService;
import ru.otus.rzdtelegrambot.service.ReplyMessagesService;
import ru.otus.rzdtelegrambot.service.UserTicketsSubscriptionService;
import ru.otus.rzdtelegrambot.utils.Emojis;

import java.util.Optional;

/**
 * Обрабатывает запрос "Отписаться" от уведомлений по ценам.
 *
 * @author Sergei Viacheslaev
 */
@Component
public class UnsubscribeTicketsInfoQueryHandler implements CallbackQueryHandler {
    private static final CallbackQueryType HANDLER_QUERY_TYPE = CallbackQueryType.UNSUBSCRIBE;
    private UserTicketsSubscriptionService subscriptionService;
    private ParseQueryDataService parseService;
    private ReplyMessagesService messagesService;
    private RZDTelegramBot telegramBot;

    public UnsubscribeTicketsInfoQueryHandler(UserTicketsSubscriptionService subscriptionService,
                                              ParseQueryDataService parseService,
                                              ReplyMessagesService messagesService,
                                              @Lazy RZDTelegramBot telegramBot) {
        this.subscriptionService = subscriptionService;
        this.parseService = parseService;
        this.messagesService = messagesService;
        this.telegramBot = telegramBot;
    }

    @Override
    public CallbackQueryType getHandlerQueryType() {
        return HANDLER_QUERY_TYPE;
    }

    @Override
    public SendMessage handleCallbackQuery(CallbackQuery callbackQuery) {
        final long chatId = callbackQuery.getMessage().getChatId();

        final String subscriptionID = parseService.parseSubscriptionIdFromUnsubscribeQuery(callbackQuery);
        Optional<UserTicketsSubscription> optionalUserSubscription = subscriptionService.getUsersSubscriptionById(subscriptionID);
        if (optionalUserSubscription.isEmpty()) {
            return messagesService.getWarningReplyMessage(chatId, "reply.query.train.userHasNoSubscription");
        }

        UserTicketsSubscription userSubscription = optionalUserSubscription.get();
        subscriptionService.deleteUserSubscription(subscriptionID);

        telegramBot.sendChangedInlineButtonText(callbackQuery,
                String.format("%s %s", Emojis.SUCCESS_UNSUBSCRIBED, UserChatButtonStatus.UNSUBSCRIBED),
                CallbackQueryType.QUERY_PROCESSED.name());

        return messagesService.getReplyMessage(chatId, "reply.query.train.unsubscribed", userSubscription.getTrainNumber(), userSubscription.getDateDepart());
    }


}
