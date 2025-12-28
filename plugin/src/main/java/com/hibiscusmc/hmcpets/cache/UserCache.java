package com.hibiscusmc.hmcpets.cache;

import com.hibiscusmc.hmcpets.api.model.UserModel;
import com.hibiscusmc.hmcpets.api.storage.Storage;
import com.hibiscusmc.hmcpets.storage.StorageHolder;
import lombok.extern.java.Log;
import org.jetbrains.annotations.NotNull;
import team.unnamed.inject.Inject;
import team.unnamed.inject.Singleton;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Log(topic = "HMCPets")
@Singleton
public class UserCache extends HashMap<UUID, UserModel> {

    @Inject
    private StorageHolder storage;

    public CompletableFuture<UserModel> fetch(@NotNull UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            UserModel user = get(uuid);

            if(user != null) {
                return user;
            }

            //Try loading user from storage
            user = storage.implementation().selectUser(uuid);
            if(user != null){ //Found the user, populate and go
                return populate(user);
            }

            user = new UserModel(uuid);
            put(uuid, user);
            storage.implementation().insertUser(user);

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

            for(UUID deletedPet : user.deletedPets()){
                impl.deletePet(deletedPet);
            }

            user.deletedPets().clear();

            for (UserModel.CachedPet cachedPet : new HashMap<>(user.pets()).values()) {
                if (cachedPet.removed() != UserModel.CachedPet.RemoveType.NONE) {
                    if (!cachedPet.cached()) {
                        impl.deletePet(cachedPet.pet().id());
                    }

                    user.removePet(cachedPet.pet());
                } else {
                    if (cachedPet.cached()) {
                        impl.insertPet(cachedPet.pet());
                        cachedPet.cached(false);
                    } else {
                        impl.savePet(cachedPet.pet());
                    }
                }
            }

            log.info("Data saved!");
        });
    }

    private UserModel populate(UserModel user) {
        Storage impl = storage.implementation();

        user.setPets(impl.selectPets(user));

        put(user.uuid(), user);

        return user;
    }

}