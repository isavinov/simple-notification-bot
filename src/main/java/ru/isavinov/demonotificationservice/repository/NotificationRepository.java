package ru.isavinov.demonotificationservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.isavinov.demonotificationservice.entity.NotificationEntity;

public interface NotificationRepository extends JpaRepository<NotificationEntity, Long> {
}
