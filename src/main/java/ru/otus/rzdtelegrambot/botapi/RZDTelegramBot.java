package ru.otus.rzdtelegrambot.botapi;

import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.otus.rzdtelegrambot.botconfig.RZDTelegramBotConfig;

import java.util.ArrayList;
import java.util.List;

public class RZDTelegramBot extends TelegramLongPollingBot {
    private RZDTelegramBotConfig botConfig;

    public RZDTelegramBot(DefaultBotOptions options, RZDTelegramBotConfig botConfig) {
        super(options);
        this.botConfig = botConfig;
    }


    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();

        if (message != null && message.getText() != null) {
            sendMsg(message, String.format("echo: %s", message.getText()));
            showMainMenu(message);
        }

    }


    public String getBotUsername() {
        return botConfig.getUserName();
    }


    public String getBotToken() {
        return botConfig.getToken();
    }


    public void sendMsg(Message message, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);

        sendMessage.setChatId(message.getChatId().toString());

        sendMessage.setText(text);
        try {
            execute(sendMessage);

        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(SendMessage sendMessage) {
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }

    public void showMainMenu(final Message request) {

        final ReplyKeyboardMarkup replyKeyboardMarkup = getMainMenuKeyboard();
        final SendMessage msg =
                createMessageWithKeyboard(request.getChatId().toString(), request.getMessageId(), replyKeyboardMarkup);


        sendMessage(msg);
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
     * сообщение отправляется с прикреплённым запросом пользователя
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
//        sendMessage.setReplyToMessageId(messageId);
        sendMessage.setText("Воспользуйтесь главным меню");
        if (replyKeyboardMarkup != null) {
            sendMessage.setReplyMarkup(replyKeyboardMarkup);
        }
        return sendMessage;
    }


}
