package ru.otus.rzdtelegrambot.botapi.handlers.callbackquery;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import ru.otus.rzdtelegrambot.botapi.RZDTelegramBot;
import ru.otus.rzdtelegrambot.model.UserTicketsSubscription;
import ru.otus.rzdtelegrambot.service.CarsProcessingService;
import ru.otus.rzdtelegrambot.service.ReplyMessagesService;
import ru.otus.rzdtelegrambot.service.UserTicketsSubscriptionService;
import ru.otus.rzdtelegrambot.utils.Emojis;
import ru.otus.rzdtelegrambot.utils.UserChatButtonStatus;

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
    private CarsProcessingService carsProcessingService;
    private ReplyMessagesService messagesService;
    private RZDTelegramBot telegramBot;

    public UnsubscribeTicketsInfoQueryHandler(UserTicketsSubscriptionService subscriptionService,
                                              CarsProcessingService carsProcessingService,
                                              ReplyMessagesService messagesService,
                                              @Lazy RZDTelegramBot telegramBot) {
        this.subscriptionService = subscriptionService;
        this.carsProcessingService = carsProcessingService;
        this.messagesService = messagesService;
        this.telegramBot = telegramBot;
    }

    @Override
    public CallbackQueryType getHandlerQueryType() {
        return HANDLER_QUERY_TYPE;
    }

    @Override
    public SendMessage handleCallbackQuery(CallbackQuery callbackQuery) {
        UserTicketsSubscription userSubscription;
        final long chatId = callbackQuery.getMessage().getChatId();

        final String subscriptionID = carsProcessingService.parseSubscriptionIDFromQuery(callbackQuery);
        Optional<UserTicketsSubscription> optionalUserSubscription = subscriptionService.getUsersSubscriptionById(subscriptionID);

        if (optionalUserSubscription.isPresent()) {
            userSubscription = optionalUserSubscription.get();
        } else {
            return messagesService.getWarningReplyMessage(chatId, "reply.query.train.userHasNoSubscription");
        }

        subscriptionService.deleteUserSubscription(subscriptionID);

        telegramBot.sendChangedInlineButtonText(callbackQuery,
                String.format("%s %s", Emojis.SUCCESS_UNSUBSCRIBED, UserChatButtonStatus.UNSUBSCRIBED),
                CallbackQueryType.QUERY_PROCESSED.name());

        return messagesService.getTrainUnsubscribedMessage(chatId, userSubscription.getTrainNumber(), userSubscription.getDateDepart());
    }


}
