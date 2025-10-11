package com.hibiscusmc.hmcpets.api.registry;

import com.hibiscusmc.hmcpets.api.HMCPets;
import com.hibiscusmc.hmcpets.api.data.ILangData;
import com.hibiscusmc.hmcpets.api.model.registry.PetRarity;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class PetRarityRegistry implements Registry<PetRarity> {

    private static final Map<String, PetRarity> REGISTRY
            = new HashMap<>();

    @Override
    public void load() {
        ILangData lang = HMCPets.instance()
                .langData();

        PetRarity.COMMON.name(lang.petsRarityCommon());
        register(PetRarity.COMMON);

        PetRarity.RARE.name(lang.petsRarityRare());
        register(PetRarity.RARE);

        PetRarity.EPIC.name(lang.petsRarityEpic());
        register(PetRarity.EPIC);

        PetRarity.LEGENDARY.name(lang.petsRarityLegendary());
        register(PetRarity.LEGENDARY);
    }

    @Override
    public void register(@NotNull PetRarity petRarity) {
        if (isRegistered(petRarity)) {
            throw new IllegalArgumentException("PetRarity " + petRarity.id() + " is already registered");
        }

        REGISTRY.put(petRarity.key().asString(), petRarity);
    }

    @Override
    public void unregister(@NotNull PetRarity petRarity) {
        if (!isRegistered(petRarity)) {
            throw new IllegalArgumentException("PetRarity " + petRarity.id() + " is not registered");
        }

        REGISTRY.remove(petRarity.key().asString());
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
    public boolean isRegistered(@NotNull PetRarity petRarity) {
        return REGISTRY.containsKey(petRarity.key().asString());
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
    public Optional<PetRarity> getRegistered(@NotNull PetRarity petRarity) {
        return Optional.ofNullable(REGISTRY.get(petRarity.key().asString()));
    }

    @Override
    public Optional<PetRarity> getRegistered(@NotNull Key key) {
        return Optional.ofNullable(REGISTRY.get(key.asString()));
    }

    @Override
    public Optional<PetRarity> getRegistered(@NotNull String str) {
        return Optional.ofNullable(REGISTRY.get(str));
    }

    @Override
    public PetRarity[] getAllRegistered() {
        return REGISTRY.values().toArray(PetRarity[]::new);
    }

}