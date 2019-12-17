package ru.otus.rzdtelegrambot.botapi.handlers;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

/**
 * @author Sergei Viacheslaev
 */
public interface InputCommandHandler {
    SendMessage handle(Message inputMsg);
    String getHandlerName();
}
