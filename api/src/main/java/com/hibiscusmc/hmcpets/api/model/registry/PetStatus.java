package com.hibiscusmc.hmcpets.api.model.registry;

public enum PetStatus {

    IDLE,
    ACTIVE,
    RESTING;

    public static PetStatus of(String status) {
        try {
            return valueOf(status.toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            return IDLE;
        }
    }

}
