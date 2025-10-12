package com.hibiscusmc.hmcpets.cache;

import com.hibiscusmc.hmcpets.api.model.UserModel;
import com.hibiscusmc.hmcpets.api.storage.Storage;
import com.hibiscusmc.hmcpets.storage.StorageHolder;
import lombok.extern.java.Log;
import org.jetbrains.annotations.NotNull;
import team.unnamed.inject.Inject;
import team.unnamed.inject.Singleton;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Log(topic = "HMCPets")
@Singleton
public class UserCache extends ConcurrentHashMap<UUID, UserModel> {

    private final Map<Integer, UserModel> usersByIndex
            = new ConcurrentHashMap<>();

    @Inject
    private StorageHolder storage;

    public CompletableFuture<UserModel> fetch(@NotNull UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            UserModel user = computeIfAbsent(
                    uuid,
                    id -> populate(storage.implementation().selectUserByUuid(id))
            );

            usersByIndex.putIfAbsent(user.id(), user);
            return user;
        });
    }

    public CompletableFuture<UserModel> fetch(int index) {
        return CompletableFuture.supplyAsync(() -> {
            UserModel user = usersByIndex.computeIfAbsent(
                    index,
                    (i) -> populate(storage.implementation().selectUser(i))
            );

            putIfAbsent(user.uuid(), user);
            return user;
        });
    }

    public CompletableFuture<Void> save(UUID key) {
        return CompletableFuture.runAsync(() -> {
            UserModel user = get(key);
            if (user == null) {
                return;
            }

            Storage impl = storage.implementation();

            impl.saveUser(user);

            for (UserModel.CachedPet cachedPet : new HashMap<>(user.pets()).values()) {
                if (cachedPet.removed() != UserModel.CachedPet.RemoveType.NONE) {
                    if (!cachedPet.cached()) {
                        impl.deletePet(cachedPet.pet());
                    }

                    user.deletePet(cachedPet.pet());
                } else {
                    if (cachedPet.cached()) {
                        impl.insertPet(cachedPet.pet());
                        cachedPet.cached(false);
                    } else {
                        impl.savePet(cachedPet.pet());
                    }
                }
            }

            for (UserModel.CachedPet cachedPet : new HashMap<>(user.activePets()).values()) {
                if (cachedPet.removed() != UserModel.CachedPet.RemoveType.NONE) {
                    if (!cachedPet.cached()) {
                        impl.deleteActivePet(user, cachedPet.pet());
                    }

                    user.deleteActivePet(cachedPet.pet());
                } else {
                    if (cachedPet.cached()) {
                        impl.insertActivePet(user, cachedPet.pet());
                        cachedPet.cached(false);
                    }
                }
            }


            for (UserModel.CachedPet cachedPet : new HashMap<>(user.favoritePets()).values()) {
                if (cachedPet.removed() != UserModel.CachedPet.RemoveType.NONE) {
                    if (!cachedPet.cached()) {
                        impl.deleteFavoritePet(user, cachedPet.pet());
                    }

                    user.deleteFavoritePet(cachedPet.pet());
                } else {
                    if (cachedPet.cached()) {
                        impl.insertFavoritePets(user, cachedPet.pet());
                        cachedPet.cached(false);
                    }
                }
            }

            log.info("Data saved!");
        });
    }

    private UserModel populate(UserModel user) {
        Storage impl = storage.implementation();

        user.setPets(impl.selectPets(user));
        user.setActivePets(impl.selectActivePets(user));
        user.setFavoritePets(impl.selectFavoritePets(user));

        return user;
    }

}