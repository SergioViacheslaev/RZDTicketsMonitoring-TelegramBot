package ru.otus.rzdtelegrambot.botapi.handlers;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.otus.rzdtelegrambot.botapi.BotState;
import ru.otus.rzdtelegrambot.service.MainMenuService;

/**
 * @author Sergei Viacheslaev
 */
@Component
public class HelpMenuHandler implements InputMessageHandler {
    private MainMenuService mainMenuService;

    public HelpMenuHandler(MainMenuService mainMenuService) {
        this.mainMenuService = mainMenuService;
    }

    @Override
    public SendMessage handle(Message message) {
        return mainMenuService.getMainMenuMessage(message, "Открыто меню помощи");

    }

    @Override
    public BotState getHandlerName() {
        return BotState.SHOW_HELP_MENU;
    }
}
