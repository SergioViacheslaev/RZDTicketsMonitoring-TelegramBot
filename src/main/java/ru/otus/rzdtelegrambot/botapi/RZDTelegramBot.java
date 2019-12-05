package ru.otus.rzdtelegrambot.botapi;

import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class RZDTelegramBot extends TelegramLongPollingBot {
    private static final String BOT_USERNAME = "@RZDTicketsMonitoringBot";
    private static final String BOT_TOKEN = "XXXXX";


    public RZDTelegramBot(DefaultBotOptions options) {
        super(options);
    }


    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();

        if (message != null && message.getText() != null) {
            sendMsg(message, String.format("echo: %s", message.getText()));
        }

    }


    public String getBotUsername() {
        return BOT_USERNAME;
    }


    public String getBotToken() {
        return BOT_TOKEN;
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


}
