package spring.infra.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import spring.infra.api.models.Booth;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BoothRepository extends JpaRepository<Booth, Long> {
    List<Booth> findAllByEventIdAndDeletedAtIsNull(UUID eventId);
    Optional<Booth> findByIdAndDeletedAtIsNull(Long id);
}
