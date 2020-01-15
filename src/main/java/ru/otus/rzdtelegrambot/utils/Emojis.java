package ru.otus.rzdtelegrambot.utils;

import com.vdurmont.emoji.EmojiParser;
import lombok.AllArgsConstructor;


/**
 * @author Sergei Viacheslaev
 */
@AllArgsConstructor
public enum Emojis {
    TRAIN(EmojiParser.parseToUnicode(":steam_locomotive:")),
    TIME_DEPART(EmojiParser.parseToUnicode(":clock8:")),
    TIME_ARRIVAL(EmojiParser.parseToUnicode(":clock3:")),
    TIME_IN_WAY(EmojiParser.parseToUnicode(":alarm_clock:")),
    SUCCESS_MARK(EmojiParser.parseToUnicode(":white_check_mark:")),
    SEARCH_FAILED(EmojiParser.parseToUnicode(":exclamation:")),
    SUCCESS_UNSUBSCRIBED(EmojiParser.parseToUnicode(":negative_squared_cross_mark:")),
    SUCCESS_SUBSCRIBED(EmojiParser.parseToUnicode(":mailbox:")),
    NOTIFICATION_BELL(EmojiParser.parseToUnicode(":bell:")),
    NOTIFICATION_INFO_MARK(EmojiParser.parseToUnicode(":information_source:"));

    private String emojiName;

    @Override
    public String toString() {
        return emojiName;
    }
}
