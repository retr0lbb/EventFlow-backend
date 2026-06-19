package spring.infra.api.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import spring.infra.api.dtos.event.CreateEventRequest;
import spring.infra.api.dtos.event.CreateEventResponse;
import spring.infra.api.enums.EventStatus;
import spring.infra.api.exceptions.CnpjAlreadyExistsException;
import spring.infra.api.models.Event;
import spring.infra.api.repository.EventRepository;

import java.sql.Timestamp;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private EventService eventService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createEvent_ValidRequest_ReturnsResponse() {
        // Arrange
        UUID userId = UUID.randomUUID();
        CreateEventRequest request = new CreateEventRequest(
                "Tech Conference 2026",
                "12345678000199",
                "A large tech gathering",
                "Silicon Valley",
                37.7749,
                -122.4194,
                500,
                1770000000,
                1770086400
        );

        Event savedEvent = new Event();
        savedEvent.setId(UUID.randomUUID());
        savedEvent.setName(request.name());
        savedEvent.setCnpj(request.cnpj());
        savedEvent.setStatus(EventStatus.DRAFT);
        savedEvent.setCreatedAt(new Timestamp(System.currentTimeMillis()));

        when(eventRepository.findByCnpj(request.cnpj())).thenReturn(Optional.empty());
        when(eventRepository.save(any(Event.class))).thenReturn(savedEvent);

        // Act
        CreateEventResponse response = eventService.createEvent(request, userId);

        // Assert
        assertNotNull(response);
        assertEquals(savedEvent.getId(), response.id());
        assertEquals(request.name(), response.name());
        assertEquals(EventStatus.DRAFT, response.status());
        verify(eventRepository, times(1)).findByCnpj(request.cnpj());
        verify(eventRepository, times(1)).save(any(Event.class));
    }

    @Test
    void createEvent_DuplicateCnpj_ThrowsException() {
        // Arrange
        UUID userId = UUID.randomUUID();
        CreateEventRequest request = new CreateEventRequest(
                "Tech Conference 2026",
                "12345678000199",
                "A large tech gathering",
                "Silicon Valley",
                37.7749,
                -122.4194,
                500,
                1770000000,
                1770086400
        );

        when(eventRepository.findByCnpj(request.cnpj())).thenReturn(Optional.of(new Event()));

        // Act & Assert
        CnpjAlreadyExistsException exception = assertThrows(CnpjAlreadyExistsException.class, () -> {
            eventService.createEvent(request, userId);
        });

        assertEquals("An event with this CNPJ already exists.", exception.getMessage());
        verify(eventRepository, times(1)).findByCnpj(request.cnpj());
        verify(eventRepository, never()).save(any(Event.class));
    }

    @Test
    void createEvent_EndsAtBeforeStartsAt_ThrowsException() {
        // Arrange
        UUID userId = UUID.randomUUID();
        CreateEventRequest request = new CreateEventRequest(
                "Invalid Event",
                "12345678000199",
                "Description",
                "Address",
                0.0,
                0.0,
                100,
                1000,
                500 // Invalid: ends before it starts
        );

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            eventService.createEvent(request, userId);
        });

        assertEquals("The event must end after it starts.", exception.getMessage());
        verify(eventRepository, never()).save(any(Event.class));
    }
}
