package com.hibiscusmc.hmcpets.api.registry;

import com.hibiscusmc.hmcpets.api.model.registry.ActionType;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ActionTypeRegistry implements Registry<ActionType> {

    private static final Map<String, ActionType> REGISTRY = new HashMap<>();

    @Override
    public void load() {
        register(ActionType.MOVES);
    }

    @Override
    public void register(@NotNull ActionType actionType) {
        if (isRegistered(actionType)) {
            throw new IllegalArgumentException("ActionType " + actionType + " is already registered");
        }

        REGISTRY.put(actionType.key().asString(), actionType);
    }

    @Override
    public void unregister(@NotNull ActionType actionType) {
        if (!isRegistered(actionType)) {
            throw new IllegalArgumentException("ActionType " + actionType + " is not registered");
        }

        REGISTRY.remove(actionType.key().asString());
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
    public boolean isRegistered(@NotNull ActionType actionType) {
        return REGISTRY.containsKey(actionType.key().asString());
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
    public Optional<ActionType> getRegistered(@NotNull ActionType actionType) {
        return Optional.ofNullable(REGISTRY.get(actionType.key().asString()));
    }

    @Override
    public Optional<ActionType> getRegistered(@NotNull Key key) {
        return Optional.ofNullable(REGISTRY.get(key.asString()));
    }

    @Override
    public Optional<ActionType> getRegistered(@NotNull String str) {
        return Optional.ofNullable(REGISTRY.get(str));
    }

    @Override
    public ActionType[] getAllRegistered() {
        return REGISTRY.values().toArray(ActionType[]::new);
    }

}