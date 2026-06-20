package spring.infra.api.enums;

public enum CheckinStatus {
    PENDING(1L, "PENDING"),
    DONE(2L, "DONE");

    private final Long id;
    private final String name;

    CheckinStatus(Long id, String name) {
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
