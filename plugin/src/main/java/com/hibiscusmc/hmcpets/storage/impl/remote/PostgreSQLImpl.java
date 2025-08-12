package com.hibiscusmc.hmcpets.storage.impl.remote;

import com.hibiscusmc.hmcpets.config.PluginConfig;
import com.hibiscusmc.hmcpets.storage.impl.SQLBasedStorage;

import java.sql.Connection;

public class PostgreSQLImpl extends SQLBasedStorage {
    @Override
    public String name() {
        return "PostgreSQL";
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
