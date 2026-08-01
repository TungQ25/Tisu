package com.example.tisu.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum SyncEntityType {
    TASK("task"),
    CATEGORY("category"),
    HABIT("habit"),
    HABIT_COMPLETION("habit_completion");

    private final String value;

    SyncEntityType(String value) {
        this.value = value;
    }

    @JsonValue
    public String value() {
        return value;
    }

    @JsonCreator
    public static SyncEntityType fromValue(String value) {
        if (value != null) {
            String normalizedValue = value.trim();
            for (SyncEntityType type : values()) {
                if (type.value.equalsIgnoreCase(normalizedValue)) {
                    return type;
                }
            }
        }
        throw new IllegalArgumentException("Unsupported sync entity type: " + value);
    }
}
