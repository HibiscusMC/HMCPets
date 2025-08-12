package com.hibiscusmc.hmcpets.model;

import lombok.Data;

import java.util.Set;
import java.util.UUID;

@Data
public class User {
    private final int id;

    private final UUID uuid;

    private Set<Pet> activePets;
    private Set<Pet> favoritePets;

    private int petPoints;
}
