package spring.infra.api.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spring.infra.api.dtos.booth.BoothResponse;
import spring.infra.api.dtos.booth.CreateBoothRequest;
import spring.infra.api.dtos.booth.UpdateBoothRequest;
import spring.infra.api.exceptions.BoothNotFoundException;
import spring.infra.api.exceptions.EventNotFoundException;
import spring.infra.api.exceptions.UnauthorizedAccessException;
import spring.infra.api.models.Booth;
import spring.infra.api.models.Event;
import spring.infra.api.repository.BoothRepository;
import spring.infra.api.repository.EventRepository;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BoothService {

    private final BoothRepository boothRepository;
    private final EventRepository eventRepository;

    public BoothService(BoothRepository boothRepository, EventRepository eventRepository) {
        this.boothRepository = boothRepository;
        this.eventRepository = eventRepository;
    }

    @Transactional
    public BoothResponse createBooth(CreateBoothRequest request, UUID userId) {
        Event event = eventRepository.findByIdAndDeletedAtIsNull(request.eventId())
                .orElseThrow(() -> new EventNotFoundException("Event not found"));

        if (!event.getCreatedBy().equals(userId)) {
            throw new UnauthorizedAccessException("You don't have permission to create a booth for this event");
        }

        Booth booth = new Booth();
        booth.setEventId(request.eventId());
        booth.setName(request.name());
        booth.setDescription(request.description());
        booth.setMapLocation(request.mapLocation());
        booth.setCapacity(request.capacity());

        Booth savedBooth = boothRepository.save(booth);
        return convertToResponse(savedBooth);
    }

    public List<BoothResponse> getAllBoothsByEvent(UUID eventId) {
        return boothRepository.findAllByEventIdAndDeletedAtIsNull(eventId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public BoothResponse getBoothById(Long id) {
        Booth booth = boothRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new BoothNotFoundException("Booth not found"));
        return convertToResponse(booth);
    }

    @Transactional
    public BoothResponse updateBooth(Long id, UpdateBoothRequest request, UUID userId) {
        Booth booth = boothRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new BoothNotFoundException("Booth not found"));

        Event event = eventRepository.findByIdAndDeletedAtIsNull(booth.getEventId())
                .orElseThrow(() -> new EventNotFoundException("Event not found"));

        if (!event.getCreatedBy().equals(userId)) {
            throw new UnauthorizedAccessException("You don't have permission to update this booth");
        }

        booth.setName(request.name());
        booth.setDescription(request.description());
        booth.setMapLocation(request.mapLocation());
        booth.setCapacity(request.capacity());

        Booth updatedBooth = boothRepository.save(booth);
        return convertToResponse(updatedBooth);
    }

    @Transactional
    public void deleteBooth(Long id, UUID userId) {
        Booth booth = boothRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new BoothNotFoundException("Booth not found"));

        Event event = eventRepository.findByIdAndDeletedAtIsNull(booth.getEventId())
                .orElseThrow(() -> new EventNotFoundException("Event not found"));

        if (!event.getCreatedBy().equals(userId)) {
            throw new UnauthorizedAccessException("You don't have permission to delete this booth");
        }

        booth.setDeletedAt(Timestamp.from(Instant.now()));
        booth.setDeletedBy(userId);

        boothRepository.save(booth);
    }

    private BoothResponse convertToResponse(Booth booth) {
        return new BoothResponse(
                booth.getId(),
                booth.getEventId(),
                booth.getName(),
                booth.getDescription(),
                booth.getMapLocation(),
                booth.getCapacity()
        );
    }
}
