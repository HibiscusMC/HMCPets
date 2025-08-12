package com.hibiscusmc.hmcpets.config;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.java.Log;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;

@Getter
@Log(topic = "HMCPets")
public class PluginConfig extends AbstractConfig {
    public PluginConfig(Path path) {
        super(path);
    }

    private StorageConfig storage;

    public void setup() {
        load();

        storage = new StorageConfig();
        String rawMethod = get("storage.method").getString("H2");

        StorageConfig.Method method = StorageConfig.Method.fromString(rawMethod);

        if (method == null) {
            log.severe("Invalid storage method set in config - " + rawMethod + " | Defaulting to H2");

            method = StorageConfig.Method.H2;
        }
        storage.method(method);

        if (method.type() == StorageConfig.MethodType.REMOTE) {
            StorageConfig.Remote remote = new StorageConfig.Remote();

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
    }

    @Getter
    @Setter(AccessLevel.PRIVATE)
    @ToString
    public static class StorageConfig {
        @NotNull
        private Method method;

        @Nullable
        private Remote remote = null;

        @NotNull
        private String database;
        @NotNull
        private String prefix;

        @Getter
        @Setter(AccessLevel.PRIVATE)
        @ToString
        public static class Remote {
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

        public enum MethodType {
            REMOTE,
            LOCAL,
            TEXT
        }

        public enum Method {
            MARIADB(MethodType.REMOTE, 3306),
            MYSQL(MethodType.REMOTE, 3306),
            POSTGRESQL(MethodType.REMOTE, 5432),
            MONGODB(MethodType.REMOTE, 27017),

            H2(MethodType.LOCAL),
            SQLITE(MethodType.LOCAL),

            JSON(MethodType.TEXT),
            YAML(MethodType.TEXT),
            CSV(MethodType.TEXT);

            private final MethodType type;
            private final int port;

            Method(MethodType type, int port) {
                this.type = type;
                this.port = port;
            }

            Method(MethodType type) {
                this(type, -1);
            }

            public MethodType type() {
                return type;
            }

            public int port() {
                return port;
            }

            static Method fromString(String method) {
                try {
                    return Method.valueOf(method.toUpperCase());
                } catch (IllegalArgumentException e) {
                    return null;
                }
            }
        }
    }
}
