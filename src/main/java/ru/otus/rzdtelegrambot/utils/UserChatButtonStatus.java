package ru.otus.rzdtelegrambot.utils;


import lombok.AllArgsConstructor;

/**Статус кнопки клавиатуры пользователя.
 *
 * @author Sergei Viacheslaev
 */
@AllArgsConstructor
public enum UserChatButtonStatus {
    SUBSCRIBED("Подписался"), UNSUBSCRIBED("Отписался");

    private String buttonStatus;

    @Override
    public String toString() {
        return buttonStatus;
    }
}

