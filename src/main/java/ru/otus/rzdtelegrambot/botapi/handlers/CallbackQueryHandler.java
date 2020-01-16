package ru.otus.rzdtelegrambot.botapi.handlers;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

/**
 * @author Sergei Viacheslaev
 */
public interface CallbackQueryHandler {
    SendMessage handleCallbackQuery(CallbackQuery callbackQuery);
}