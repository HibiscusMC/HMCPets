package com.hibiscusmc.hmcpets.api.registry;

import com.hibiscusmc.hmcpets.api.model.mobtypes.ModelEngineMobType;
import com.hibiscusmc.hmcpets.api.model.mobtypes.MythicMobsMobType;
import com.hibiscusmc.hmcpets.api.model.mobtypes.VanillaMobType;
import com.hibiscusmc.hmcpets.api.model.registry.MobType;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class MobTypeRegistry implements Registry<MobType> {

    private static final Map<String, MobType> REGISTRY = new HashMap<>();

    @Override
    public void load() {
        register(new VanillaMobType());
        register(new ModelEngineMobType());
        register(new MythicMobsMobType());
    }

    @Override
    public void register(@NotNull MobType mobType) {
        if (isRegistered(mobType)) {
            throw new IllegalArgumentException("ActionType " + mobType + " is already registered");
        }

        REGISTRY.put(mobType.key().asString(), mobType);
    }

    @Override
    public void unregister(@NotNull MobType mobType) {
        if (!isRegistered(mobType)) {
            throw new IllegalArgumentException("MobType " + mobType + " is not registered");
        }

        REGISTRY.remove(mobType.key().asString());
    }

    @Override
    public void unregister(@NotNull Key key) {
        if (!isRegistered(key)) {
            throw new IllegalArgumentException("Key " + key + " is not registered");
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
    public boolean isRegistered(@NotNull MobType mobType) {
        return REGISTRY.containsKey(mobType.key().asString());
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
    public Optional<MobType> getRegistered(@NotNull MobType mobType) {
        return Optional.ofNullable(REGISTRY.get(mobType.key().asString()));
    }

    @Override
    public Optional<MobType> getRegistered(@NotNull Key key) {
        return Optional.ofNullable(REGISTRY.get(key.asString()));
    }

    @Override
    public Optional<MobType> getRegistered(String str) {
        if(str == null) return Optional.empty();

        //Could have different namespaces - need to retrieve the key manually
        String retrievedKey = REGISTRY.keySet().stream().filter(s -> s.endsWith(str)).findFirst().orElse(null);
        if(retrievedKey == null) return Optional.empty();

        return Optional.ofNullable(REGISTRY.get(retrievedKey));
    }

    @Override
    public MobType[] getAllRegistered() {
        return REGISTRY.values().toArray(MobType[]::new);
    }

}