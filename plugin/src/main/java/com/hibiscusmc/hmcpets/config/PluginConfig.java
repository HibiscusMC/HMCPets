package com.hibiscusmc.hmcpets.config;

import com.hibiscusmc.hmcpets.api.data.*;
import com.hibiscusmc.hmcpets.api.storage.StorageMethod;
import com.hibiscusmc.hmcpets.api.storage.StorageMethodType;
import com.hibiscusmc.hmcpets.config.internal.AbstractConfig;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.java.Log;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.unnamed.inject.Singleton;

import java.nio.file.Path;

@Getter
@Log(topic = "HMCPets")
@Singleton
public class PluginConfig extends AbstractConfig implements IPluginData {

    public PluginConfig(Path path) {
        super(path);
    }

    private Storage storage;
    private Pets pets;
    private Users users;

    public void setup() {
        load();

        storage = new Storage();
        String rawMethod = get("storage.method").getString("H2");

        StorageMethod method = StorageMethod.fromString(rawMethod);

        if (method == null) {
            log.severe("Invalid storage method set in config - " + rawMethod + " | Defaulting to H2");

            method = StorageMethod.H2;
        }

        storage.method(method);

        if (method.type() == StorageMethodType.REMOTE) {
            Storage.Remote remote = new Storage.Remote();

            String uri = get("storage.remote.uri").getString("");

            if (!uri.isEmpty()) {
                remote.uri(uri);
            } else {
                remote.address(get("storage.remote.address").getString("localhost"))
                        .port(get("storage.remote.port").getInt(method.port()))
                        .username(get("storage.remote.username").getString("root"))
                        .password(get("storage.remote.password").getString(""));
            }

            storage.remote(remote);
        }

        storage.database(get("storage.database").getString("hmcpets"))
                .prefix(get("storage.prefix").getString("hmcpets_"));

        pets = new Pets();
        pets.maxActive(get("pets.max-active").getInt(3));

        users = new Users();
        users.allowNegativePetPointsBalance(get("allow-negative-pet-points").getBoolean(false));
        users.allowDuplicatePets(get("allow-duplicate-pets").getBoolean(true));
    }

    @Getter
    @Setter(AccessLevel.PRIVATE)
    @ToString
    public static class Pets implements IPluginPetData {

        int maxActive;

    }


    @Getter
    @Setter(AccessLevel.PRIVATE)
    @ToString
    public static class Users implements IPluginUsersData {

        boolean allowNegativePetPointsBalance;
        boolean allowDuplicatePets;

    }

    @Getter
    @Setter(AccessLevel.PRIVATE)
    @ToString
    public static class Storage implements IPluginStorageData {

        @NotNull
        private StorageMethod method;

        @Nullable
        private Remote remote = null;

        @NotNull
        private String database;

        @NotNull
        private String prefix;

        @Getter
        @Setter(AccessLevel.PRIVATE)
        @ToString
        public static class Remote implements IPluginRemoteStorageData {

            @Nullable
            private String uri;

            @NotNull
            private String address;

            private int port;

            @NotNull
            private String username;

            @NotNull
            private String password;

        }

    }

}