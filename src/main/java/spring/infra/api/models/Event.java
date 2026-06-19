package spring.infra.api.models;

import jakarta.persistence.*;
import org.w3c.dom.Text;
import spring.infra.api.enums.EventStatus;

import java.sql.Timestamp;
import java.util.UUID;

@Table(name = "events")
@Entity
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;

    private Text description;

    private String address;

    private Double latitude;

    private Double longtude;

    private Integer maxParticipants;

    private Integer startsAt;

    private Integer endsAt;

    private UUID createdBy;

    private Timestamp createdAt;

    private UUID deletedBy;

    private Timestamp deletedAt;

    private EventStatus status;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Text getDescription() {
        return description;
    }

    public void setDescription(Text description) {
        this.description = description;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongtude() {
        return longtude;
    }

    public void setLongtude(Double longtude) {
        this.longtude = longtude;
    }

    public Integer getMaxParticipants() {
        return maxParticipants;
    }

    public void setMaxParticipants(Integer maxParticipants) {
        this.maxParticipants = maxParticipants;
    }

    public Integer getStartsAt() {
        return startsAt;
    }

    public void setStartsAt(Integer startsAt) {
        this.startsAt = startsAt;
    }

    public Integer getEndsAt() {
        return endsAt;
    }

    public void setEndsAt(Integer endsAt) {
        this.endsAt = endsAt;
    }

    public UUID getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(UUID createdBy) {
        this.createdBy = createdBy;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public UUID getDeletedBy() {
        return deletedBy;
    }

    public void setDeletedBy(UUID deletedBy) {
        this.deletedBy = deletedBy;
    }

    public Timestamp getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Timestamp deletedAt) {
        this.deletedAt = deletedAt;
    }

    public EventStatus getStatus() {
        return status;
    }

    public void setStatus(EventStatus status) {
        this.status = status;
    }
}
