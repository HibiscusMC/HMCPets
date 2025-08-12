package com.hibiscusmc.hmcpets.storage.impl.local;

import com.hibiscusmc.hmcpets.config.PluginConfig;
import com.hibiscusmc.hmcpets.storage.impl.SQLBasedStorage;
import com.hibiscusmc.hmcpets.storage.impl.StorageImpl;

import java.sql.Connection;

public class SQLiteImpl extends SQLBasedStorage {
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