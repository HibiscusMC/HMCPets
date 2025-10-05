package com.hibiscusmc.hmcpets.api.model;

import lombok.Data;

import java.util.Set;
import java.util.UUID;

@Data
public class UserModel {

    private final int id;

    private final UUID uuid;

    private Set<PetModel> activePets;
    private Set<PetModel> favoritePets;

    private int petPoints;

}