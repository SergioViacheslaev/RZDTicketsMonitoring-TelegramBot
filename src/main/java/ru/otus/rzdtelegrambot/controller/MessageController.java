package ru.otus.rzdtelegrambot.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.otus.rzdtelegrambot.botapi.RZDTelegramBot;
import ru.otus.rzdtelegrambot.service.MainMenuService;
import ru.otus.rzdtelegrambot.service.TrainSearchService;
import ru.otus.rzdtelegrambot.session.UserState;
import ru.otus.rzdtelegrambot.session.UsersSession;

/**
 * @author UnAfraid
 */
@Slf4j
@RestController
public class MessageController {
    private RZDTelegramBot telegramBot;

    @Autowired
    private TrainSearchService trainSearchService;

    @Autowired
    private MainMenuService mainMenuService;

    @Autowired
    private UsersSession usersSession;

    public MessageController(RZDTelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }


    @RequestMapping(value = "/", method = RequestMethod.POST)
    @ResponseBody
    public BotApiMethod<?> onUpdateReceived(@RequestBody Update update) {
        Message message = update.getMessage();

        if (message != null && message.hasText()) {

            parseIncomingMessage(message);


        }

        return null;
    }

    private void parseIncomingMessage(Message message) {
        if (message != null && message.hasText()) {

            int userId = message.getFrom().getId();


            switch (message.getText()) {
                case "Найти поезда":
                    usersSession.setUserStateByID(message.getFrom().getId(), UserState.ASK_STATION_DEPART);
                    break;
                case "Помощь":
                    telegramBot.sendMessageToChat(userId, "Открыт раздел Помощь");
                    return;
            }

            parseUserSessionState(message);


        }


    }

    private void parseUserSessionState(Message message) {
        int userId = message.getFrom().getId();
        UserState userState = usersSession.getUserStateByID(userId);

        if (userState == null) {
            usersSession.setUserStateByID(userId, UserState.SHOW_MAIN_MENU);
            mainMenuService.showMainMenu(message);
            return;
        }

        if (userState.equals(UserState.SHOW_MAIN_MENU)) {
            mainMenuService.showMainMenu(message);
            return;
        }


        if (userState.equals(UserState.ASK_STATION_DEPART) ||
                userState.equals(UserState.ASK_STATION_ARRIVAL) ||
                userState.equals(UserState.ASK_DATE_DEPART)) {

            trainSearchService.createUsersSearchRequest(message);
            return;
        }

        if (userState.equals(UserState.TRAIN_INFO_RESPONCE_AWAITING)) {
            telegramBot.sendMessageToChat(message.getChatId(), "Начинаю поиск билетов по заданным критериям...");
            return;
        }


    }


}
