package com.hibiscusmc.hmcpets.model;

import com.hibiscusmc.hmcpets.i18n.LangEntry;

import java.util.Set;

public record Collar(
        String id, LangEntry name, Set<PetType> allowedTypes, int requiredLevel,
        long cooldown, Type type, Ability ability
) {
    public enum Type {
        BUFF,
        HEAL,
        SHIELD,
        ABILITY
    }

    public static abstract class Ability {
        public abstract void execute(User user, Pet pet, Collar collar);
    }
}
