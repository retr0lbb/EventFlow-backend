package spring.infra.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import spring.infra.api.models.Event;
import spring.infra.api.models.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EventRepository extends JpaRepository<Event, UUID> {
    Optional<Event> findByCreatedBy(UUID userId);
    Optional<Event> findByCnpj(String cnpj);
    List<Event> findAllByDeletedAtIsNull();
    Optional<Event> findByIdAndDeletedAtIsNull(UUID id);
}
