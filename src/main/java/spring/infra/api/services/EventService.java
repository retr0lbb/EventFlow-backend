package spring.infra.api.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spring.infra.api.dtos.event.CreateEventRequest;
import spring.infra.api.dtos.event.CreateEventResponse;
import spring.infra.api.dtos.event.EventResponse;
import spring.infra.api.dtos.event.UpdateEventRequest;
import spring.infra.api.enums.EventStatus;
import spring.infra.api.models.Event;
import spring.infra.api.repository.EventRepository;
import spring.infra.api.exceptions.CnpjAlreadyExistsException;
import spring.infra.api.exceptions.EventNotFoundException;
import spring.infra.api.exceptions.UnauthorizedAccessException;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
public class EventService {

    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Transactional
    public EventResponse createEvent(CreateEventRequest request, UUID userId) {
        if (request.endsAt() <= request.startsAt()) {
            throw new IllegalArgumentException("The event must end after it starts.");
        }

        if (eventRepository.findByCnpj(request.cnpj()).isPresent()) {
            throw new CnpjAlreadyExistsException("An event with this CNPJ already exists.");
        }

        Event evt = new Event();

        evt.setName(request.name());
        evt.setCnpj(request.cnpj());
        evt.setDescription(request.description());
        evt.setAddress(request.address());
        evt.setLatitude(request.latitude());
        evt.setLongitude(request.longitude());
        evt.setMaxParticipants(request.maxParticipants());
        evt.setStartsAt(request.startsAt());
        evt.setEndsAt(request.endsAt());
        
        evt.setCreatedBy(userId);
        evt.setCreatedAt(Timestamp.from(Instant.now()));
        evt.setStatus(EventStatus.DRAFT);

        Event savedEvent = eventRepository.save(evt);

        return convertToResponse(savedEvent);
    }

    public List<EventResponse> getAllEvents() {
        return eventRepository.findAllByDeletedAtIsNull().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public EventResponse getEventById(UUID id) {
        return eventRepository.findByIdAndDeletedAtIsNull(id)
                .map(this::convertToResponse)
                .orElseThrow(() -> new EventNotFoundException("Event not found"));
    }

    @Transactional
    public EventResponse updateEvent(UUID id, UpdateEventRequest request, UUID userId) {
        Event event = eventRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new EventNotFoundException("Event not found"));

        if (!event.getCreatedBy().equals(userId)) {
            throw new UnauthorizedAccessException("You don't have permission to update this event");
        }

        if (request.endsAt() <= request.startsAt()) {
            throw new IllegalArgumentException("The event must end after it starts.");
        }

        if (!event.getCnpj().equals(request.cnpj()) && eventRepository.findByCnpj(request.cnpj()).isPresent()) {
            throw new CnpjAlreadyExistsException("An event with this CNPJ already exists.");
        }

        event.setName(request.name());
        event.setCnpj(request.cnpj());
        event.setDescription(request.description());
        event.setAddress(request.address());
        event.setLatitude(request.latitude());
        event.setLongitude(request.longitude());
        event.setMaxParticipants(request.maxParticipants());
        event.setStartsAt(request.startsAt());
        event.setEndsAt(request.endsAt());

        Event updatedEvent = eventRepository.save(event);
        return convertToResponse(updatedEvent);
    }

    @Transactional
    public void deleteEvent(UUID id, UUID userId) {
        Event event = eventRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new EventNotFoundException("Event not found"));

        if (!event.getCreatedBy().equals(userId)) {
            throw new UnauthorizedAccessException("You don't have permission to delete this event");
        }

        event.setDeletedAt(Timestamp.from(Instant.now()));
        event.setDeletedBy(userId);

        eventRepository.save(event);
    }

    private EventResponse convertToResponse(Event event) {
        return new EventResponse(
                event.getId(),
                event.getName(),
                event.getCnpj(),
                event.getDescription(),
                event.getAddress(),
                event.getLatitude(),
                event.getLongitude(),
                event.getMaxParticipants(),
                event.getStartsAt(),
                event.getEndsAt(),
                event.getStatus(),
                event.getCreatedAt().getTime()
        );
    }
}
vent.getEndsAt(),
                event.getStatus(),
                event.getCreatedAt().getTime()
        );
    }
}
