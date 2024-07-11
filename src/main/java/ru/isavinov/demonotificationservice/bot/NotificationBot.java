package ru.isavinov.demonotificationservice.bot;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.isavinov.demonotificationservice.service.NotificationService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class NotificationBot implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {

    private final String token;

    private final TelegramClient telegramClient;

    private final NotificationService notificationService;

    public NotificationBot(@Value("${botToken}") String token, TelegramClient telegramClient, NotificationService notificationService) {
        this.token = token;
        this.telegramClient = telegramClient;
        this.notificationService = notificationService;
    }

    @Override
    public String getBotToken() {
        return token;
    }

    @Override
    public LongPollingUpdateConsumer getUpdatesConsumer() {
        return this;
    }

    @Override
    public void consume(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            String username = update.getMessage().getFrom().getUserName();

            String answer ="";
            if (messageText.equals("/start")) {
                answer = "Привет "+ username+ "! Чтобы создать напоминание пришли сообщение" +
                        "формата дд.мм.гггг чч:мм текст напоминания";
            } else {
                String regex = "(\\d{2}\\.\\d{2}\\.\\d{4}\\s\\d{2}:\\d{2})\\s(.+)";

                // Компиляция регулярного выражения
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(messageText);

                // Проверка совпадения и извлечение данных
                if (matcher.matches()) {
                    String dateTime = matcher.group(1); // Извлечение даты
                    String message = matcher.group(2); // Извлечение текста

                    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

                    notificationService.createNotification(chatId, LocalDateTime.parse(dateTime, dateTimeFormatter), message);

                    answer="Напоминание сохранено!";

                } else {
                    answer="Неверный формат. Пришли сообщение формата:" +
                            "дд.мм.гггг чч:мм текст напоминания";
                }
            }

            SendMessage message = SendMessage
                    .builder()
                    .chatId(chatId)
                    .text(answer)
                    .build();
            try {
                telegramClient.execute(message);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }
}
