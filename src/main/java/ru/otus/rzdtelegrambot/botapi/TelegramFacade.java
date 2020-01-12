package ru.otus.rzdtelegrambot.botapi;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.otus.rzdtelegrambot.cache.UserDataCache;

/**
 * @author Sergei Viacheslaev
 */
@Service
@Slf4j
public class TelegramFacade {
    private UserDataCache userDatabase;

    private BotStateContext botStateContext;

    public TelegramFacade(UserDataCache userDatabase, BotStateContext botStateContext) {
        this.userDatabase = userDatabase;
        this.botStateContext = botStateContext;
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
            case "Помощь":
                botState = BotState.SHOW_HELP_MENU;
                break;
            default:
                botState = userDatabase.getUserBotState(userId);
                break;
        }

        //Назначаем нашему боту полученное состояние
        botStateContext.setCurrentState(botState);

        //Обрабатываем сообщение от пользователя, находясь уже в нужном состоянии
        replyMessage = botStateContext.processInputMessage(message);

        //Сохраняем в БД последнее состояние после обработки сообщения
        userDatabase.saveUserBotState(userId, botStateContext.getCurrentState());

        return replyMessage;
    }

    private SendMessage processCallbackQuery(CallbackQuery callbackQuery) {
        String queryReply;

        log.info("CallbackQuery data:{}", callbackQuery.getData());
        log.info("CallbackQuery Message:{}", callbackQuery.getMessage().getText());


        String[] queryData = callbackQuery.getData().split("\\|");
        switch (queryData[0]) {
            case "subscribe":
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

         /*   System.out.println("--------------\n");
            System.out.println(Arrays.toString(trainInfo));
            System.out.println(stationDepart);
            System.out.println(stationArrival);


            String callbackMessage = callbackQuery.getMessage().getText();

            String stationDepart = callbackMessage.substring(callbackMessage.lastIndexOf("Отправление:") + 13,
                    callbackMessage.indexOf(",")).trim();
            String stationArrival = callbackMessage.substring(callbackMessage.lastIndexOf("Прибытие:") + 10,
                    callbackMessage.lastIndexOf(",")).trim();*/


    }
}
