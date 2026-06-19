package spring.infra.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import spring.infra.api.models.BoothSession;
import java.util.List;
import java.util.Optional;

public interface BoothSessionRepository extends JpaRepository<BoothSession, Long> {
    List<BoothSession> findAllByBoothIdAndDeletedAtIsNull(Long boothId);
    Optional<BoothSession> findByIdAndDeletedAtIsNull(Long id);
}
