package ru.otus.rzdtelegrambot.botapi.handlers.help;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.otus.rzdtelegrambot.botapi.handlers.InputCommandHandler;

/**
 * @author Sergei Viacheslaev
 */
@Component
public class HelpCommandHandler implements InputCommandHandler {
    @Override
    public String getHandlerName() {
        return "Помощь";
    }

    @Override
    public SendMessage handle(Message inputMsg) {
        return new SendMessage(inputMsg.getChatId(), "Открыт раздел помощи");
    }
}
