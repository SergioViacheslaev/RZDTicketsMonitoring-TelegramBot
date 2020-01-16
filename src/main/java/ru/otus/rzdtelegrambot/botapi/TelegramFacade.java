package ru.otus.rzdtelegrambot.botapi;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.otus.rzdtelegrambot.botapi.handlers.callbackquery.CallbackQueryFacade;
import ru.otus.rzdtelegrambot.cache.UserDataCache;
import ru.otus.rzdtelegrambot.service.UserTicketsSubscriptionService;

/**
 * @author Sergei Viacheslaev
 */
@Service
@Slf4j
public class TelegramFacade {
    private UserDataCache userDataCache;
    private BotStateContext botStateContext;
    private UserTicketsSubscriptionService subscribeService;
    private CallbackQueryFacade callbackQueryFacade;
    private RZDTelegramBot telegramBot;

    public TelegramFacade(UserDataCache userDataCache, BotStateContext botStateContext,
                          UserTicketsSubscriptionService subscribeService, CallbackQueryFacade callbackQueryFacade,
                          @Lazy RZDTelegramBot telegramBot) {
        this.userDataCache = userDataCache;
        this.botStateContext = botStateContext;
        this.subscribeService = subscribeService;
        this.callbackQueryFacade = callbackQueryFacade;
        this.telegramBot = telegramBot;
    }

    public SendMessage handleUpdate(Update update) {
        SendMessage replyMessage = null;

        if (update.hasCallbackQuery()) {
            log.info("New callbackQuery from User: {} with data: {}", update.getCallbackQuery().getFrom().getUserName(),
                    update.getCallbackQuery().getData());
            return callbackQueryFacade.processCallbackQuery(update.getCallbackQuery());
        }


        Message message = update.getMessage();
        if (message != null && message.hasText()) {
            log.info("New message from User:{}, chatId: {},  with text: {}",
                    message.getFrom().getUserName(), message.getChatId(), message.getText());
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


}
