package com.hibiscusmc.hmcpets.storage.impl.local;

import com.hibiscusmc.hmcpets.config.PluginConfig;
import com.hibiscusmc.hmcpets.pet.PetConfig;
import com.hibiscusmc.hmcpets.storage.impl.SQLBasedStorage;
import team.unnamed.inject.Inject;

import java.sql.Connection;

public class SQLiteStorage extends SQLBasedStorage {

    @Inject
    public SQLiteStorage(PluginConfig pluginConfig, PetConfig petConfig) {
        super(pluginConfig, petConfig);
    }

    @Override
    public String name() {
        return "SQLite";
    }

    @Override
    public void initialize(PluginConfig.StorageConfig config) {

    }

    @Override
    public void close() {

    }

    @Override
    public Connection getConnection() {
        return null;
    }
}