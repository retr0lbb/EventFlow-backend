package spring.infra.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import spring.infra.api.enums.CheckinStatus;
import spring.infra.api.models.UserEventCheckin;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserEventCheckinRepository extends JpaRepository<UserEventCheckin, UUID> {
    List<UserEventCheckin> findByEventId(UUID eventId);
    List<UserEventCheckin> findByUserId(UUID userId);
    Optional<UserEventCheckin> findByEventIdAndUserId(UUID eventId, UUID userId);
    List<UserEventCheckin> findByEventIdAndStatus(UUID eventId, CheckinStatus status);
}
