package ru.otus.rzdtelegrambot.botapi.handlers;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.otus.rzdtelegrambot.botapi.BotState;
import ru.otus.rzdtelegrambot.service.MainMenuService;

/**
 * Отправка сообщения с клавиатурой и текстом
 *
 * @author Sergei Viacheslaev
 */
@Component
public class MainMenuHandler implements InputMessageHandler {
    private MainMenuService mainMenuService;

    public MainMenuHandler(MainMenuService mainMenuService) {
        this.mainMenuService = mainMenuService;
    }

    @Override
    public SendMessage handle(Message message) {
        return mainMenuService.getMainMenuMessage(message, "Воспользуйтесь главным меню");
    }

    @Override
    public BotState getHandlerName() {
        return BotState.SHOW_MAIN_MENU;
    }


}
