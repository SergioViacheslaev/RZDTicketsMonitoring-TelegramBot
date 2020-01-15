package ru.otus.rzdtelegrambot.utils;

import lombok.AllArgsConstructor;

/**
 * Chat button types.
 *
 * @author Sergei Viacheslaev
 */
@AllArgsConstructor
public enum UserChatButtonType {
    SUBSCRIBE("subscribe"), UNSUBSCRIBE("unsubscribe");

    private String buttonName;

    @Override
    public String toString() {
        return buttonName;
    }
}
