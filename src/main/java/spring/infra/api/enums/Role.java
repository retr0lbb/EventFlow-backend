package spring.infra.api.enums;

public enum Role {
    VISITANT(1L, "VISITANT"),
    STAND_CREATOR(2L, "STAND_CREATOR"),
    EVENT_CREATOR(3L, "EVENT_CREATOR");

    private final Long id;
    private final String name;

    Role(Long id, String name) {
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
