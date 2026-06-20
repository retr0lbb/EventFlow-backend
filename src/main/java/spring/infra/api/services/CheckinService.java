package spring.infra.api.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import spring.infra.api.dtos.checkin.CheckinResponse;
import spring.infra.api.dtos.checkin.CreateCheckinRequest;
import spring.infra.api.enums.CheckinStatus;
import spring.infra.api.exceptions.EventAtFullCapacityException;
import spring.infra.api.exceptions.UnauthorizedAccessException;
import spring.infra.api.models.Event;
import spring.infra.api.models.User;
import spring.infra.api.models.UserEventCheckin;
import spring.infra.api.repository.EventRepository;
import spring.infra.api.repository.UserEventCheckinRepository;
import spring.infra.api.repository.UserRepository;

import java.sql.Timestamp;
import java.util.Optional;
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

        Optional<UserEventCheckin> existing = checkinRepository.findByEventIdAndUserId(event.getId(), userId);

        if (existing.isPresent()) {
            UserEventCheckin existingCheckin = existing.get();

            System.out.println("CreateCheckin -> checkin existente encontrado. URL: http://localhost:5173/confirmation/" + existingCheckin.getId() + " | status: " + existingCheckin.getStatus());

            if (existingCheckin.getStatus() == CheckinStatus.DONE) {
                throw new IllegalArgumentException("You already checked in for this event");
            }

            return new CheckinResponse(
                    existingCheckin.getId(),
                    existingCheckin.getEvent().getId(),
                    existingCheckin.getUser().getId(),
                    existingCheckin.getStatus(),
                    existingCheckin.getCreatedAt().getTime(),
                    "You already have a pending check-in for this event"
            );
        }

        UserEventCheckin checkin = new UserEventCheckin();
        checkin.setEvent(event);
        checkin.setUser(user);
        checkin.setStatus(CheckinStatus.PENDING);

        UserEventCheckin saved = checkinRepository.save(checkin);

        String confirmationUrl = "http://localhost:5173/confirmation/" + saved.getId();
        System.out.println("CreateCheckin -> checkin novo criado. URL: " + confirmationUrl);

        emailService.sendEventPresenceConfirmation(user.getEmail(), confirmationUrl, event.getName());

        return new CheckinResponse(
                saved.getId(),
                saved.getEvent().getId(),
                saved.getUser().getId(),
                saved.getStatus(),
                saved.getCreatedAt().getTime(),
                null
        );
    }

    @Transactional
    public CheckinResponse validateCheckin(UUID checkinId, UUID loggedUserId) {
        UserEventCheckin checkin = checkinRepository.findById(checkinId)
                .orElseThrow(() -> new IllegalArgumentException("Checkin not found"));

        if (!checkin.getUser().getId().equals(loggedUserId)) {
            throw new UnauthorizedAccessException("This is not your checkin");
        }

        if (checkin.getStatus() == CheckinStatus.DONE) {
            return new CheckinResponse(
                    checkin.getId(),
                    checkin.getEvent().getId(),
                    checkin.getUser().getId(),
                    checkin.getStatus(),
                    checkin.getCreatedAt().getTime(),
                    "You already checked in for this event"
            );
        }

        Event event = checkin.getEvent();

        long doneCount = checkinRepository.findByEventIdAndStatus(event.getId(), CheckinStatus.DONE).size();

        if (event.getMaxParticipants() != null && doneCount >= event.getMaxParticipants()) {
            throw new EventAtFullCapacityException("Event is at full capacity. Sorry, no more check-ins can be validated.");
        }

        checkin.setStatus(CheckinStatus.DONE);
        checkin.setCheckedInAt(new Timestamp(System.currentTimeMillis()));

        UserEventCheckin saved = checkinRepository.save(checkin);

        return new CheckinResponse(
                saved.getId(),
                saved.getEvent().getId(),
                saved.getUser().getId(),
                saved.getStatus(),
                saved.getCreatedAt().getTime(),
                null
        );
    }
}