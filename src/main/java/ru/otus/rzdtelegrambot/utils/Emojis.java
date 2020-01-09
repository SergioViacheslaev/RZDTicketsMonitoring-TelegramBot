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
    SEARCH_FINISHED(EmojiParser.parseToUnicode(":white_check_mark:"));

    private String emojiName;

    @Override
    public String toString() {
        return emojiName;
    }
}
