package com.hibiscusmc.hmcpets.storage;

import com.hibiscusmc.hmcpets.config.PluginConfig;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;
import team.unnamed.inject.Singleton;

@Getter
@Setter
@Log(topic = "HMCPets")
@Singleton
public class StorageHolder {
    private com.hibiscusmc.hmcpets.storage.impl.Storage implementation;

    public String name() {
        return implementation.name();
    }

    public void initialize(PluginConfig.StorageConfig config) {
        if (implementation == null) {
            throw new IllegalStateException("Storage has not been initialized");
        }

        try {
            implementation.initialize(config);
        } catch (Exception ex) {
            log.severe("Couldn't initialize " + implementation.name() + " storage. Shutting down...");
            throw new RuntimeException(ex);
        }
    }

    public void close() {
        if (implementation == null) {
            throw new IllegalStateException("Storage has not been initialized");
        }

        try {
            implementation.close();
        } catch (Exception ex) {
            log.severe("Couldn't close " + implementation.name() + " storage");
            throw new RuntimeException(ex);
        }
    }
}
