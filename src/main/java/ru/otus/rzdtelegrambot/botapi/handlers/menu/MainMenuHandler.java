package ru.otus.rzdtelegrambot.botapi.handlers.menu;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.otus.rzdtelegrambot.botapi.BotState;
import ru.otus.rzdtelegrambot.botapi.handlers.InputMessageHandler;
import ru.otus.rzdtelegrambot.service.MainMenuService;
import ru.otus.rzdtelegrambot.service.ReplyMessagesService;

@Component
@RequiredArgsConstructor
public class MainMenuHandler implements InputMessageHandler {
    private final ReplyMessagesService messagesService;
    private final MainMenuService mainMenuService;

    @Override
    public SendMessage handle(Message message) {
        return mainMenuService.getMainMenuMessage(message.getChatId(), messagesService.getReplyText("reply.mainMenu.welcomeMessage"));
    }

    @Override
    public BotState getHandlerName() {
        return BotState.SHOW_MAIN_MENU;
    }

}
