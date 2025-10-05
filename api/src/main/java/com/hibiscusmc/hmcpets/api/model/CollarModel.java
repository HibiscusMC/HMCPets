package com.hibiscusmc.hmcpets.api.model;

import com.hibiscusmc.hmcpets.api.model.enums.CollarType;
import com.hibiscusmc.hmcpets.api.model.enums.PetType;

import java.util.Set;

public record CollarModel(
        String id, String name, Set<PetType> allowedTypes, int requiredLevel,
        long cooldown, CollarType type, Ability ability
) {

    public static abstract class Ability {
        public abstract void execute(UserModel user, PetModel pet, CollarModel collar);
    }

}