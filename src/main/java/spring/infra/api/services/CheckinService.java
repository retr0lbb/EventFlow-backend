package spring.infra.api.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spring.infra.api.dtos.checkin.CheckinResponse;
import spring.infra.api.dtos.checkin.CreateCheckinRequest;
import spring.infra.api.enums.CheckinStatus;
import spring.infra.api.models.Event;
import spring.infra.api.models.User;
import spring.infra.api.models.UserEventCheckin;
import spring.infra.api.repository.EventRepository;
import spring.infra.api.repository.UserEventCheckinRepository;
import spring.infra.api.repository.UserRepository;

import java.util.UUID;

@Service
public class CheckinService {

    private final UserEventCheckinRepository checkinRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    public CheckinService(UserEventCheckinRepository checkinRepository, EventRepository eventRepository, UserRepository userRepository, EmailService emailService) {
        this.checkinRepository = checkinRepository;
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    @Transactional
    public CheckinResponse createCheckin(CreateCheckinRequest request, UUID userId) {
        Event event = eventRepository.findById(request.eventId())
                .orElseThrow(() -> new IllegalArgumentException("Event not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        UserEventCheckin checkin = new UserEventCheckin();
        checkin.setEvent(event);
        checkin.setUser(user);
        checkin.setStatus(CheckinStatus.PENDING);

        UserEventCheckin saved = checkinRepository.save(checkin);

        emailService.sendEventPresenceConfirmation(user.getEmail(), "randomUrl", event.getName());

        return new CheckinResponse(
                saved.getId(),
                saved.getEvent().getId(),
                saved.getUser().getId(),
                saved.getStatus(),
                saved.getCreatedAt().getTime()
        );
    }
}
