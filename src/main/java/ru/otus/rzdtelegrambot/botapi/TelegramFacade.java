package ru.otus.rzdtelegrambot.botapi;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.otus.rzdtelegrambot.cache.UserDataCache;
import ru.otus.rzdtelegrambot.service.SubscribeTicketsInfoService;

/**
 * @author Sergei Viacheslaev
 */
@Service
@Slf4j
public class TelegramFacade {
    private UserDataCache userDataCache;
    private BotStateContext botStateContext;
    private SubscribeTicketsInfoService subscribeService;

    public TelegramFacade(UserDataCache userDataCache, BotStateContext botStateContext, SubscribeTicketsInfoService subscribeService) {
        this.userDataCache = userDataCache;
        this.botStateContext = botStateContext;
        this.subscribeService = subscribeService;
    }

    public SendMessage handleUpdate(Update update) {
        SendMessage replyMessage = null;

        if (update.hasCallbackQuery()) {
            return processCallbackQuery(update.getCallbackQuery());
        }


        Message message = update.getMessage();
        if (message != null && message.hasText()) {
            log.info("New message from user:{} with text:{}", message.getFrom().getFirstName(), message.getText());
            replyMessage = handleInputMessage(message);
        }

        return replyMessage;
    }

    private SendMessage handleInputMessage(Message message) {
        String inputMsg = message.getText();
        int userId = message.getFrom().getId();
        BotState botState;
        SendMessage replyMessage;

        switch (inputMsg) {
            case "Найти поезда":
                botState = BotState.TRAINS_SEARCH;
                break;
            case "Мои подписки":
                botState = BotState.SHOW_SUBSCRIPTIONS_MENU;
                break;
            case "Помощь":
                botState = BotState.SHOW_HELP_MENU;
                break;
            default:
                botState = userDataCache.getUserBotState(userId);
                break;
        }

        //Назначаем нашему боту полученное состояние
        botStateContext.setCurrentState(botState);

        //Обрабатываем сообщение от пользователя, находясь уже в нужном состоянии
        replyMessage = botStateContext.processInputMessage(message);

        //Сохраняем в БД последнее состояние после обработки сообщения
        userDataCache.saveUserBotState(userId, botStateContext.getCurrentState());

        return replyMessage;
    }

    private SendMessage processCallbackQuery(CallbackQuery callbackQuery) {
        String queryReply;

        log.info("CallbackQuery data:{}", callbackQuery.getData());
        log.info("CallbackQuery Message:{}", callbackQuery.getMessage().getText());


        String[] queryData = callbackQuery.getData().split("\\|");
        switch (queryData[0]) {
            case "subscribe":
                subscribeService.saveUserSubscription(callbackQuery);
                queryReply = "Вы успешно подписаны !";
                break;
            case "unsubscribe":
                queryReply = "Вы успешно отписаны от обновления цен !";
                break;
            default:
                queryReply = "Не могу разобрать ваш запрос";
                break;
        }

        return new SendMessage(callbackQuery.getMessage().getChatId(), queryReply);


    }
}
