package com.hibiscusmc.hmcpets.api.registry;

import com.hibiscusmc.hmcpets.api.HMCPets;
import com.hibiscusmc.hmcpets.api.data.ILangData;
import com.hibiscusmc.hmcpets.api.model.registry.PetType;
import me.lojosho.hibiscuscommons.hooks.Hooks;
import net.kyori.adventure.key.Key;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class PetTypeRegistry implements AbstractRegistry<PetType> {

    private static final Map<String, PetType> REGISTRY
            = new HashMap<>();

    public static final PetType AQUATIC_TYPE
            = new PetType(AbstractRegistry.withDefaultKey("aquatic"));
    public static final PetType BEAST_TYPE
            = new PetType(AbstractRegistry.withDefaultKey("beast"));
    public static final PetType MAGIC_TYPE
            = new PetType(AbstractRegistry.withDefaultKey("magic"));
    public static final PetType CRITTER_TYPE
            = new PetType(AbstractRegistry.withDefaultKey("critter"));

    @Override
    public void load() {
        ILangData lang = HMCPets.instance()
                .langData();

        AQUATIC_TYPE.name(lang.petsTypeAquatic());
        AQUATIC_TYPE.registerSkill(ActionTypeRegistry.MOVES, pet -> {
            LivingEntity entity = pet.entity();
            if (entity == null) {
                return;
            }

            if (!entity.isUnderWater()) {
                return;
            }

            if (entity.canBreatheUnderwater()) {
                return;
            }

            if (entity.getRemainingAir() >= entity.getMaximumAir()) {
                return;
            }

            entity.setRemainingAir(entity.getMaximumAir());
        });
        AQUATIC_TYPE.registerCraving(Objects.requireNonNull(Hooks.getItem("PAPER")));
        register(AQUATIC_TYPE);

        BEAST_TYPE.name(lang.petsTypeBeast());
        register(BEAST_TYPE);

        MAGIC_TYPE.name(lang.petsTypeMagic());
        register(MAGIC_TYPE);

        CRITTER_TYPE.name(lang.petsTypeCritter());
        register(CRITTER_TYPE);
    }

    @Override
    public void register(@NotNull PetType petType) {
        if (isRegistered(petType)) {
            throw new IllegalArgumentException("PetType " + petType.id() + " is already registered");
        }

        REGISTRY.put(petType.key().asString(), petType);
    }

    @Override
    public void unregister(@NotNull PetType petType) {
        if (!isRegistered(petType)) {
            throw new IllegalArgumentException("PetType " + petType.id() + " is not registered");
        }

        REGISTRY.remove(petType.key().asString());
    }

    @Override
    public void unregister(@NotNull Key key) {
        if (!isRegistered(key)) {
            throw new IllegalArgumentException("Key " + key.asString() + " is not registered");
        }

        REGISTRY.remove(key.asString());
    }

    @Override
    public void unregister(@NotNull String str) {
        if (str.isEmpty()) {
            throw new IllegalArgumentException("Key cannot be empty");
        }

        if (!isRegistered(str)) {
            throw new IllegalArgumentException("Key " + str + " is not registered");
        }

        REGISTRY.remove(str);
    }

    @Override
    public boolean isRegistered(@NotNull PetType petType) {
        return REGISTRY.containsKey(petType.key().asString());
    }

    @Override
    public boolean isRegistered(@NotNull Key key) {
        return REGISTRY.containsKey(key.asString());
    }

    @Override
    public boolean isRegistered(@NotNull String str) {
        return REGISTRY.containsKey(str);
    }

    @Override
    public Optional<PetType> getRegistered(@NotNull PetType petType) {
        return Optional.ofNullable(REGISTRY.get(petType.key().asString()));
    }

    @Override
    public Optional<PetType> getRegistered(@NotNull Key key) {
        return Optional.ofNullable(REGISTRY.get(key.asString()));
    }

    @Override
    public Optional<PetType> getRegistered(@NotNull String str) {
        return Optional.ofNullable(REGISTRY.get(str));
    }

    @Override
    public PetType[] getAllRegistered() {
        return REGISTRY.values().toArray(PetType[]::new);
    }

}