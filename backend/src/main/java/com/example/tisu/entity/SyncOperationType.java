package com.example.tisu.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum SyncOperationType {
    CREATE("create"),
    UPDATE("update"),
    DELETE("delete"),
    RESTORE("restore");

    private final String value;

    SyncOperationType(String jsonValue) {
        this.value = jsonValue;
    }

    @JsonValue
    public String value() {
        return value;
    }

    @JsonCreator
    public static SyncOperationType fromValue(String value) {
        if (value != null) {
            String normalizedValue = value.trim();
            for (SyncOperationType type : values()) {
                if (type.value.equalsIgnoreCase(normalizedValue)) {
                    return type;
                }
            }
        }
        throw new IllegalArgumentException("Unsupported sync operation: " + value);
    }
}
