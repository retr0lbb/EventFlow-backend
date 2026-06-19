package spring.infra.api.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spring.infra.api.dtos.session.CreateEventSessionRequest;
import spring.infra.api.dtos.session.EventSessionResponse;
import spring.infra.api.dtos.session.UpdateEventSessionRequest;
import spring.infra.api.exceptions.EventNotFoundException;
import spring.infra.api.exceptions.UnauthorizedAccessException;
import spring.infra.api.models.Event;
import spring.infra.api.models.EventSession;
import spring.infra.api.repository.EventRepository;
import spring.infra.api.repository.EventSessionRepository;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class EventSessionService {

    private final EventSessionRepository eventSessionRepository;
    private final EventRepository eventRepository;

    public EventSessionService(EventSessionRepository eventSessionRepository, EventRepository eventRepository) {
        this.eventSessionRepository = eventSessionRepository;
        this.eventRepository = eventRepository;
    }

    @Transactional
    public EventSessionResponse createSession(CreateEventSessionRequest request, UUID userId) {
        Event event = eventRepository.findByIdAndDeletedAtIsNull(request.eventId())
                .orElseThrow(() -> new EventNotFoundException("Event not found"));

        if (!event.getCreatedBy().equals(userId)) {
            throw new UnauthorizedAccessException("You don't have permission to create a session for this event");
        }

        if (request.endsAt().before(request.startsAt())) {
            throw new IllegalArgumentException("Session must end after it starts");
        }

        EventSession session = new EventSession();
        session.setEventId(request.eventId());
        session.setTitle(request.title());
        session.setDescription(request.description());
        session.setStartsAt(request.startsAt());
        session.setEndsAt(request.endsAt());
        session.setCapacity(request.capacity());

        EventSession savedSession = eventSessionRepository.save(session);
        return convertToResponse(savedSession);
    }

    public List<EventSessionResponse> getAllSessionsByEvent(UUID eventId) {
        return eventSessionRepository.findAllByEventIdAndDeletedAtIsNull(eventId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public EventSessionResponse getSessionById(Long id) {
        EventSession session = eventSessionRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new RuntimeException("Session not found"));
        return convertToResponse(session);
    }

    @Transactional
    public EventSessionResponse updateSession(Long id, UpdateEventSessionRequest request, UUID userId) {
        EventSession session = eventSessionRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        Event event = eventRepository.findByIdAndDeletedAtIsNull(session.getEventId())
                .orElseThrow(() -> new EventNotFoundException("Event not found"));

        if (!event.getCreatedBy().equals(userId)) {
            throw new UnauthorizedAccessException("You don't have permission to update this session");
        }

        if (request.endsAt().before(request.startsAt())) {
            throw new IllegalArgumentException("Session must end after it starts");
        }

        session.setTitle(request.title());
        session.setDescription(request.description());
        session.setStartsAt(request.startsAt());
        session.setEndsAt(request.endsAt());
        session.setCapacity(request.capacity());

        EventSession updatedSession = eventSessionRepository.save(session);
        return convertToResponse(updatedSession);
    }

    @Transactional
    public void deleteSession(Long id, UUID userId) {
        EventSession session = eventSessionRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        Event event = eventRepository.findByIdAndDeletedAtIsNull(session.getEventId())
                .orElseThrow(() -> new EventNotFoundException("Event not found"));

        if (!event.getCreatedBy().equals(userId)) {
            throw new UnauthorizedAccessException("You don't have permission to delete this session");
        }

        session.setDeletedAt(Timestamp.from(Instant.now()));
        session.setDeletedBy(userId);

        eventSessionRepository.save(session);
    }

    private EventSessionResponse convertToResponse(EventSession session) {
        return new EventSessionResponse(
                session.getId(),
                session.getEventId(),
                session.getTitle(),
                session.getDescription(),
                session.getStartsAt(),
                session.getEndsAt(),
                session.getCapacity(),
                session.getCreatedAt().getTime(),
                session.getUpdatedAt().getTime()
        );
    }
}
