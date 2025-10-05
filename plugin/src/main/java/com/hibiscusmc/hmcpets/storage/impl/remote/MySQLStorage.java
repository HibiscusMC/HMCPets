package com.hibiscusmc.hmcpets.storage.impl.remote;

import com.hibiscusmc.hmcpets.api.data.IStorageData;
import com.hibiscusmc.hmcpets.config.PluginConfig;
import com.hibiscusmc.hmcpets.config.PetConfig;
import com.hibiscusmc.hmcpets.storage.impl.SQLBasedStorage;
import team.unnamed.inject.Inject;

import java.sql.Connection;

public class MySQLStorage extends SQLBasedStorage {

    @Inject
    public MySQLStorage(PluginConfig pluginConfig, PetConfig petConfig) {
        super(pluginConfig, petConfig);
    }

    @Override
    public String name() {
        return "MySQL";
    }

    @Override
    public void initialize(IStorageData config) {

    }

    @Override
    public void close() {

    }

    @Override
    public Connection getConnection() {
        return null;
    }
}