package spring.infra.api.services;

import org.springframework.stereotype.Service;
import spring.infra.api.dtos.event.CreateEventRequest;
import spring.infra.api.dtos.event.CreateEventResponse;
import spring.infra.api.enums.EventStatus;
import spring.infra.api.repository.EventRepository;


@Service
public class EventService {

    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public CreateEventResponse createEvent(CreateEventRequest request){

    }
}
