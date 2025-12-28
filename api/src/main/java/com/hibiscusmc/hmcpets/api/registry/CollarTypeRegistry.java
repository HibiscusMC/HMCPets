package com.hibiscusmc.hmcpets.api.registry;

import com.hibiscusmc.hmcpets.api.model.registry.CollarType;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CollarTypeRegistry implements Registry<CollarType> {

    private static final Map<String, CollarType> REGISTRY
            = new HashMap<>();

    @Override
    public void load() {
        register(CollarType.BUFF);
        register(CollarType.SHIELD);
        register(CollarType.HEAL);
        register(CollarType.ABILITY);
    }

    @Override
    public void register(@NotNull CollarType collarType) {
        if (isRegistered(collarType)) {
            throw new IllegalArgumentException("CollarType " + collarType + " is already registered");
        }

        REGISTRY.put(collarType.key().asString(), collarType);
    }

    @Override
    public void unregister(@NotNull CollarType collarType) {
        if (!isRegistered(collarType)) {
            throw new IllegalArgumentException("CollarType " + collarType + " is not registered");
        }

        REGISTRY.remove(collarType.key().asString());
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
    public boolean isRegistered(@NotNull CollarType collarType) {
        return REGISTRY.containsKey(collarType.key().asString());
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
    public Optional<CollarType> getRegistered(@NotNull CollarType collarType) {
        return Optional.ofNullable(REGISTRY.get(collarType.key().asString()));
    }

    @Override
    public Optional<CollarType> getRegistered(@NotNull Key key) {
        return Optional.ofNullable(REGISTRY.get(key.asString()));
    }

    @Override
    public Optional<CollarType> getRegistered(String str) {
        if(str == null) return Optional.empty();

        //Could have different namespaces - need to retrieve the key manually
        String retrievedKey = REGISTRY.keySet().stream().filter(s -> s.endsWith(str)).findFirst().orElse(null);
        if(retrievedKey == null) return Optional.empty();

        return Optional.ofNullable(REGISTRY.get(retrievedKey));
    }

    @Override
    public CollarType[] getAllRegistered() {
        return REGISTRY.values().toArray(CollarType[]::new);
    }
    
}