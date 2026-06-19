package spring.infra.api.models;

import jakarta.persistence.*;
import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Table(name = "booth_sessions")
public class BoothSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "booth_session_id")
    private Long id;

    @Column(name = "booth_id", nullable = false)
    private Long boothId;

    @Column(name = "event_sessions_id")
    private Long eventSessionId;

    @Column(nullable = false)
    private String title;

    private String description;

    @Column(name = "starts_at", nullable = false)
    private Timestamp startsAt;

    @Column(name = "ends_at", nullable = false)
    private Timestamp endsAt;

    private Integer capacity;

    @Column(name = "deleted_at")
    private Timestamp deletedAt;

    @Column(name = "deleted_by")
    private UUID deletedBy;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBoothId() {
        return boothId;
    }

    public void setBoothId(Long boothId) {
        this.boothId = boothId;
    }

    public Long getEventSessionId() {
        return eventSessionId;
    }

    public void setEventSessionId(Long eventSessionId) {
        this.eventSessionId = eventSessionId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Timestamp getStartsAt() {
        return startsAt;
    }

    public void setStartsAt(Timestamp startsAt) {
        this.startsAt = startsAt;
    }

    public Timestamp getEndsAt() {
        return endsAt;
    }

    public void setEndsAt(Timestamp endsAt) {
        this.endsAt = endsAt;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public Timestamp getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Timestamp deletedAt) {
        this.deletedAt = deletedAt;
    }

    public UUID getDeletedBy() {
        return deletedBy;
    }

    public void setDeletedBy(UUID deletedBy) {
        this.deletedBy = deletedBy;
    }
}
