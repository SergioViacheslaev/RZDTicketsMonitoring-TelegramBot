package ru.otus.rzdtelegrambot.botapi.handlers.callbackquery;

import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * Статус кнопки клавиатуры пользователя.
 *
 * @author Sergei Viacheslaev
 */
@ToString
@RequiredArgsConstructor
public enum UserChatButtonStatus {
    SUBSCRIBED("Подписался"), UNSUBSCRIBED("Отписался");

    private final String buttonStatus;
}

