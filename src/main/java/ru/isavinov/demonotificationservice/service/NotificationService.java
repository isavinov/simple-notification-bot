package ru.isavinov.demonotificationservice.service;

import org.springframework.stereotype.Service;
import ru.isavinov.demonotificationservice.entity.NotificationEntity;
import ru.isavinov.demonotificationservice.repository.NotificationRepository;

import java.time.LocalDateTime;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public void createNotification(Long chatId, LocalDateTime notificationDate, String message){
        NotificationEntity notification = new NotificationEntity(chatId, notificationDate, message);
        notificationRepository.save(notification);

    }
}
