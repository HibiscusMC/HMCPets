package com.hibiscusmc.hmcpets.api.registry;

import com.hibiscusmc.hmcpets.api.HMCPets;
import com.hibiscusmc.hmcpets.api.data.ILangData;
import com.hibiscusmc.hmcpets.api.model.registry.ActionType;
import com.hibiscusmc.hmcpets.api.model.registry.PetType;
import me.lojosho.hibiscuscommons.hooks.Hooks;
import net.kyori.adventure.key.Key;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class PetTypeRegistry implements Registry<PetType> {

    private static final Map<String, PetType> REGISTRY
            = new HashMap<>();

    @Override
    public void load() {
        ILangData lang = HMCPets.instance()
                .langData();

        //TODO: Implement actual skills with support for MythicMobs ones
        PetType.AQUATIC.name(lang.petsTypeAquatic());
        PetType.AQUATIC.registerSkill(ActionType.MOVES, pet -> {
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
        PetType.AQUATIC.registerCraving(Objects.requireNonNull(Hooks.getItem("PAPER")));
        register(PetType.AQUATIC);

        PetType.BEAST.name(lang.petsTypeBeast());
        register(PetType.BEAST);

        PetType.MAGIC.name(lang.petsTypeMagic());
        register(PetType.MAGIC);

        PetType.CRITTER.name(lang.petsTypeCritter());
        register(PetType.CRITTER);
    }

    @Override
    public void register(@NotNull PetType petType) {
        if (isRegistered(petType)) {
            throw new IllegalArgumentException("PetType " + petType.id() + " is already registered");
        }

        System.out.println("Registering PetType " + petType.key().asString());

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
    public Optional<PetType> getRegistered(String str) {
        if(str == null) return Optional.empty();

        //Could have different namespaces - need to retrieve the key manually
        String retrievedKey = REGISTRY.keySet().stream().filter(s -> s.endsWith(str)).findFirst().orElse(null);
        if(retrievedKey == null) return Optional.empty();

        return Optional.ofNullable(REGISTRY.get(retrievedKey));
    }

    @Override
    public PetType[] getAllRegistered() {
        return REGISTRY.values().toArray(PetType[]::new);
    }

}