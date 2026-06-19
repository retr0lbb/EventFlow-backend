package spring.infra.api.enums;

public enum EventStatus {
    DRAFT(1L, "DRAFT"),
    PUBLISHED(2L, "PUBLISHED"),
    FINISHED(3L, "FINISHED");

    private final Long id;
    private final String name;

    EventStatus(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }
    public String getName() {
        return name;
    }
}
