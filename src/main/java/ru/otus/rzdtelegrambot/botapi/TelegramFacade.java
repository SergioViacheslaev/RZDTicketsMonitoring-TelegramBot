package ru.otus.rzdtelegrambot.botapi;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.otus.rzdtelegrambot.cache.UserDataCache;
import ru.otus.rzdtelegrambot.service.SubscribeTicketsInfoService;
import ru.otus.rzdtelegrambot.utils.Emojis;
import ru.otus.rzdtelegrambot.utils.UserChatButtonStatus;

/**
 * @author Sergei Viacheslaev
 */
@Service
@Slf4j
public class TelegramFacade {
    private UserDataCache userDataCache;
    private BotStateContext botStateContext;
    private SubscribeTicketsInfoService subscribeService;
    //todo: Возможно стоить вынести обработку Callback в отдельный сервис
    private RZDTelegramBot telegramBot;

    public TelegramFacade(UserDataCache userDataCache, BotStateContext botStateContext,
                          SubscribeTicketsInfoService subscribeService, @Lazy RZDTelegramBot telegramBot) {
        this.userDataCache = userDataCache;
        this.botStateContext = botStateContext;
        this.subscribeService = subscribeService;
        this.telegramBot = telegramBot;
    }

    public SendMessage handleUpdate(Update update) {
        SendMessage replyMessage = null;

        if (update.hasCallbackQuery()) {
            log.info("Получен запрос callbackQuery: {}", update.getCallbackQuery().getData());
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

        String[] queryData = callbackQuery.getData().split("\\|");
        String buttonType = queryData[0];
        String trainNumber = queryData[1];
        switch (buttonType) {
            case "subscribe":
                String dateDepart = queryData[2];
                subscribeService.saveUserSubscription(callbackQuery);
                telegramBot.sendChangedInlineButtonText(callbackQuery,
                        String.format("%s %s", Emojis.SUCCESS_SUBSCRIBED, UserChatButtonStatus.SUBSCRIBED), "buttonPressed");
                queryReply = String.format("Оформлена подписка на поезд №%s отправлением %s", trainNumber, dateDepart);
                break;
            case "unsubscribe":
                subscribeService.deleteUserSubscription(callbackQuery);
                telegramBot.sendChangedInlineButtonText(callbackQuery,
                        String.format("%s %s", Emojis.SUCCESS_UNSUBSCRIBED, UserChatButtonStatus.UNSUBSCRIBED), "buttonPressed");
                queryReply = String.format("Удалена подписка на поезд №%s", trainNumber);
                break;
            default:
                queryReply = String.format("%sНе могу разобрать ваш запрос", Emojis.SEARCH_FAILED);
                break;
        }

        return new SendMessage(callbackQuery.getMessage().getChatId(), queryReply);


    }


}
