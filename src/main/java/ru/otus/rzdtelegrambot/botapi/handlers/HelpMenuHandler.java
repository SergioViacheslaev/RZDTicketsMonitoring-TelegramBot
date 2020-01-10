package ru.otus.rzdtelegrambot.botapi.handlers;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.otus.rzdtelegrambot.botapi.BotState;
import ru.otus.rzdtelegrambot.botapi.RZDTelegramBot;
import ru.otus.rzdtelegrambot.service.MainMenuService;

/**
 * @author Sergei Viacheslaev
 */
@Component
public class HelpMenuHandler implements InputMessageHandler {
    private MainMenuService mainMenuService;
    private RZDTelegramBot rzdTelegramBot;

    public HelpMenuHandler(MainMenuService mainMenuService, @Lazy RZDTelegramBot rzdTelegramBot) {
        this.mainMenuService = mainMenuService;
        this.rzdTelegramBot = rzdTelegramBot;
    }

    @Override
    public SendMessage handle(Message message) {
        //todo: убрать лишний вывод
         rzdTelegramBot.sendInlineKeyBoardMessage(message.getChatId(),"Поезд 153","Подписаться","137|Москва|Санкт-Петербург|23.01.2020");

        return mainMenuService.getMainMenuMessage(message, "Открыто меню помощи");
    }

    @Override
    public BotState getHandlerName() {
        return BotState.SHOW_HELP_MENU;
    }
}
