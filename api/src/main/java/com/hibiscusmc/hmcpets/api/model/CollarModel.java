package com.hibiscusmc.hmcpets.api.model;

import com.hibiscusmc.hmcpets.api.model.registry.CollarType;
import com.hibiscusmc.hmcpets.api.model.registry.PetType;

import java.util.Set;

public record CollarModel(
        String id, String name, Set<PetType> allowedTypes, int requiredLevel,
        long cooldown, CollarType type, Ability ability
) {

    public static abstract class Ability {
        public abstract void execute(UserModel user, PetModel pet, CollarModel collar);
    }

}