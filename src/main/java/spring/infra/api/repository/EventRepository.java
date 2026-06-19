package spring.infra.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import spring.infra.api.models.Event;
import spring.infra.api.models.User;

import java.util.Optional;
import java.util.UUID;

public interface EventRepository extends JpaRepository<Event, UUID> {
    Optional<Event> findByCreator(UUID userId);
}
