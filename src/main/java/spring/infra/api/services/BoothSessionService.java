package spring.infra.api.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spring.infra.api.dtos.session.BoothSessionResponse;
import spring.infra.api.dtos.session.CreateBoothSessionRequest;
import spring.infra.api.dtos.session.UpdateBoothSessionRequest;
import spring.infra.api.exceptions.BoothNotFoundException;
import spring.infra.api.exceptions.EventNotFoundException;
import spring.infra.api.exceptions.UnauthorizedAccessException;
import spring.infra.api.models.Booth;
import spring.infra.api.models.BoothSession;
import spring.infra.api.models.Event;
import spring.infra.api.repository.BoothRepository;
import spring.infra.api.repository.BoothSessionRepository;
import spring.infra.api.repository.EventRepository;
import spring.infra.api.repository.EventSessionRepository;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BoothSessionService {

    private final BoothSessionRepository boothSessionRepository;
    private final BoothRepository boothRepository;
    private final EventRepository eventRepository;
    private final EventSessionRepository eventSessionRepository;

    public BoothSessionService(
            BoothSessionRepository boothSessionRepository,
            BoothRepository boothRepository,
            EventRepository eventRepository,
            EventSessionRepository eventSessionRepository
    ) {
        this.boothSessionRepository = boothSessionRepository;
        this.boothRepository = boothRepository;
        this.eventRepository = eventRepository;
        this.eventSessionRepository = eventSessionRepository;
    }

    @Transactional
    public BoothSessionResponse createSession(CreateBoothSessionRequest request, UUID userId) {
        Booth booth = boothRepository.findByIdAndDeletedAtIsNull(request.boothId())
                .orElseThrow(() -> new BoothNotFoundException("Booth not found"));

        Event event = eventRepository.findByIdAndDeletedAtIsNull(booth.getEventId())
                .orElseThrow(() -> new EventNotFoundException("Event not found"));

        if (!event.getCreatedBy().equals(userId)) {
            throw new UnauthorizedAccessException("You don't have permission to create a session for this booth");
        }

        if (request.eventSessionId() != null) {
            eventSessionRepository.findByIdAndDeletedAtIsNull(request.eventSessionId())
                    .orElseThrow(() -> new RuntimeException("Event session not found"));
        }

        if (request.endsAt().before(request.startsAt())) {
            throw new IllegalArgumentException("Session must end after it starts");
        }

        BoothSession session = new BoothSession();
        session.setBoothId(request.boothId());
        session.setEventSessionId(request.eventSessionId());
        session.setTitle(request.title());
        session.setDescription(request.description());
        session.setStartsAt(request.startsAt());
        session.setEndsAt(request.endsAt());
        session.setCapacity(request.capacity());

        BoothSession savedSession = boothSessionRepository.save(session);
        return convertToResponse(savedSession);
    }

    public List<BoothSessionResponse> getAllSessionsByBooth(Long boothId) {
        return boothSessionRepository.findAllByBoothIdAndDeletedAtIsNull(boothId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public BoothSessionResponse getSessionById(Long id) {
        BoothSession session = boothSessionRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new RuntimeException("Booth session not found"));
        return convertToResponse(session);
    }

    @Transactional
    public BoothSessionResponse updateSession(Long id, UpdateBoothSessionRequest request, UUID userId) {
        BoothSession session = boothSessionRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new RuntimeException("Booth session not found"));

        Booth booth = boothRepository.findByIdAndDeletedAtIsNull(session.getBoothId())
                .orElseThrow(() -> new BoothNotFoundException("Booth not found"));

        Event event = eventRepository.findByIdAndDeletedAtIsNull(booth.getEventId())
                .orElseThrow(() -> new EventNotFoundException("Event not found"));

        if (!event.getCreatedBy().equals(userId)) {
            throw new UnauthorizedAccessException("You don't have permission to update this booth session");
        }

        if (request.eventSessionId() != null) {
            eventSessionRepository.findByIdAndDeletedAtIsNull(request.eventSessionId())
                    .orElseThrow(() -> new RuntimeException("Event session not found"));
        }

        if (request.endsAt().before(request.startsAt())) {
            throw new IllegalArgumentException("Session must end after it starts");
        }

        session.setEventSessionId(request.eventSessionId());
        session.setTitle(request.title());
        session.setDescription(request.description());
        session.setStartsAt(request.startsAt());
        session.setEndsAt(request.endsAt());
        session.setCapacity(request.capacity());

        BoothSession updatedSession = boothSessionRepository.save(session);
        return convertToResponse(updatedSession);
    }

    @Transactional
    public void deleteSession(Long id, UUID userId) {
        BoothSession session = boothSessionRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new RuntimeException("Booth session not found"));

        Booth booth = boothRepository.findByIdAndDeletedAtIsNull(session.getBoothId())
                .orElseThrow(() -> new BoothNotFoundException("Booth not found"));

        Event event = eventRepository.findByIdAndDeletedAtIsNull(booth.getEventId())
                .orElseThrow(() -> new EventNotFoundException("Event not found"));

        if (!event.getCreatedBy().equals(userId)) {
            throw new UnauthorizedAccessException("You don't have permission to delete this booth session");
        }

        session.setDeletedAt(Timestamp.from(Instant.now()));
        session.setDeletedBy(userId);

        boothSessionRepository.save(session);
    }

    private BoothSessionResponse convertToResponse(BoothSession session) {
        return new BoothSessionResponse(
                session.getId(),
                session.getBoothId(),
                session.getEventSessionId(),
                session.getTitle(),
                session.getDescription(),
                session.getStartsAt(),
                session.getEndsAt(),
                session.getCapacity()
        );
    }
}
