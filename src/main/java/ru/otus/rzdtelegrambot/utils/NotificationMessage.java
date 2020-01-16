package ru.otus.rzdtelegrambot.utils;

import lombok.AllArgsConstructor;

/**
 * Готовые сообщения для уеедомлений.
 *
 * @author Sergei Viacheslaev
 */
@AllArgsConstructor
public enum NotificationMessage {
    HANDLE_KEYBOARD_QUERY_FAILED(String.format("%s Не могу обработать запрос от клавиатуры", Emojis.NOTIFICATION_MARK_FAILED)),
    USER_HAS_SUBSCRIPTION(String.format("%s У вас уже есть такая подписка.", Emojis.NOTIFICATION_MARK_FAILED)),
    DELETE_SUBSCRIPTION_FAILED(String.format("%s Не могу найти и удалить подписку", Emojis.NOTIFICATION_MARK_FAILED)),
    QUERY_WAS_PROCESSED(String.format("%s Ваш запрос уже был обработан.", Emojis.NOTIFICATION_MARK_FAILED)),
    TICKET_SEARCH_DATEDEPART_OUTDATE(String.format("%s Дата отправления находится за пределами периода предварительной продажи.",
            Emojis.NOTIFICATION_MARK_FAILED)),
    STATION_SEARCH_FAILED(String.format("%s Станция не найдена, повторите ввод.", Emojis.NOTIFICATION_MARK_FAILED));

    private String messageText;

    @Override
    public String toString() {
        return messageText;
    }
}
