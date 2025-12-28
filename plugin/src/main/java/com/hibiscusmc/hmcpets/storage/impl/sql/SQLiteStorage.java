package com.hibiscusmc.hmcpets.storage.impl.sql;

import com.hibiscusmc.hmcpets.api.HMCPets;
import com.hibiscusmc.hmcpets.api.data.IPluginStorageData;
import com.hibiscusmc.hmcpets.config.PetConfig;
import com.hibiscusmc.hmcpets.config.PluginConfig;
import com.hibiscusmc.hmcpets.storage.impl.SQLBasedStorage;
import team.unnamed.inject.Inject;

import java.sql.Connection;

public class SQLiteStorage extends SQLBasedStorage {

    @Inject
    public SQLiteStorage(HMCPets instance, PluginConfig pluginConfig, PetConfig petConfig) {
        super(instance, pluginConfig, petConfig);
    }

    @Override
    public String name() {
        return "SQLite";
    }

    @Override
    public void initialize(IPluginStorageData config) {

    }

    @Override
    public void close() {

    }

    @Override
    public Connection getConnection() {
        return null;
    }

}