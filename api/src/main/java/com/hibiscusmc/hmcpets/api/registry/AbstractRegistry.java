package com.hibiscusmc.hmcpets.api.registry;

import com.hibiscusmc.hmcpets.api.HMCPets;
import net.kyori.adventure.key.Key;
import org.intellij.lang.annotations.Subst;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public interface AbstractRegistry<T> {

    void load();

    void register(@NotNull T t);

    void unregister(@NotNull T t);

    void unregister(@NotNull Key key);

    void unregister(@NotNull String str);

    boolean isRegistered(@NotNull T t);

    boolean isRegistered(@NotNull Key key);

    boolean isRegistered(@NotNull String str);

    Optional<T> getRegistered(@NotNull T t);

    Optional<T> getRegistered(@NotNull Key key);

    Optional<T> getRegistered(@NotNull String str);

    T[] getAllRegistered();

    static Key withDefaultKey(@Subst("value") @NotNull String string) {
        return Key.key(HMCPets.instance(), string);
    }

}