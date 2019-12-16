package ru.otus.rzdtelegrambot.service;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import ru.otus.rzdtelegrambot.botapi.RZDTelegramBot;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Sergei Viacheslaev
 */
@Service
public class MainMenuService {

    private RZDTelegramBot rzdBot;

    public MainMenuService(RZDTelegramBot rzdTelegramBot) {
        this.rzdBot = rzdTelegramBot;
    }

    public void showMainMenu(final Message request) {
        final ReplyKeyboardMarkup replyKeyboardMarkup = getMainMenuKeyboard();
        final SendMessage msg =
                createMessageWithKeyboard(request.getChatId().toString(), request.getMessageId(), replyKeyboardMarkup);

        rzdBot.sendMessage(msg);
    }

    public SendMessage sendMainMenuMessage(final Message request) {
        final ReplyKeyboardMarkup replyKeyboardMarkup = getMainMenuKeyboard();
        final SendMessage mainMenuMessage =
                createMessageWithKeyboard(request.getChatId().toString(), request.getMessageId(), replyKeyboardMarkup);

        return mainMenuMessage;
    }

    private ReplyKeyboardMarkup getMainMenuKeyboard() {

        final ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);


        List<KeyboardRow> keyboard = new ArrayList<>();


        KeyboardRow row = new KeyboardRow();
        row.add(new KeyboardButton("Найти поезда"));
        row.add(new KeyboardButton("Помощь"));
        keyboard.add(row);
        replyKeyboardMarkup.setKeyboard(keyboard);
        return replyKeyboardMarkup;
    }

    /**
     * Отправка сообщения с клавиатурой и текстом
     *
     * @param chatId
     * @param messageId
     * @param replyKeyboardMarkup
     * @return
     */
    private SendMessage createMessageWithKeyboard(final String chatId,
                                                  final Integer messageId,
                                                  final ReplyKeyboardMarkup replyKeyboardMarkup) {
        final SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(chatId);
        sendMessage.setText("Воспользуйтесь главным меню");
        if (replyKeyboardMarkup != null) {
            sendMessage.setReplyMarkup(replyKeyboardMarkup);
        }
        return sendMessage;
    }
}
