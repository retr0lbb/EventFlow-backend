package spring.infra.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import spring.infra.api.models.EventSession;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EventSessionRepository extends JpaRepository<EventSession, Long> {
    List<EventSession> findAllByEventIdAndDeletedAtIsNull(UUID eventId);
    Optional<EventSession> findByIdAndDeletedAtIsNull(Long id);
}
