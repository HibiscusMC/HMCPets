package com.hibiscusmc.hmcpets.storage;

import com.hibiscusmc.hmcpets.config.PluginConfig;
import com.hibiscusmc.hmcpets.service.Service;
import com.hibiscusmc.hmcpets.storage.impl.StorageImpl;
import com.hibiscusmc.hmcpets.storage.impl.local.H2Impl;
import com.hibiscusmc.hmcpets.storage.impl.local.SQLiteImpl;
import com.hibiscusmc.hmcpets.storage.impl.remote.MariaDBImpl;
import com.hibiscusmc.hmcpets.storage.impl.remote.MongoDBImpl;
import com.hibiscusmc.hmcpets.storage.impl.remote.MySQLImpl;
import com.hibiscusmc.hmcpets.storage.impl.remote.PostgreSQLImpl;
import com.hibiscusmc.hmcpets.storage.impl.text.CSVImpl;
import com.hibiscusmc.hmcpets.storage.impl.text.JSONImpl;
import com.hibiscusmc.hmcpets.storage.impl.text.YAMLImpl;
import lombok.extern.java.Log;
import team.unnamed.inject.Inject;

@Log(topic = "HMCPets")
public class StorageService extends Service {
    @Inject
    private PluginConfig pluginConfig;
    @Inject
    private Storage storage;

    protected StorageService() {
        super("Storage");
    }

    @Override
    protected void initialize() {
        PluginConfig.StorageConfig storageConfig = pluginConfig.storage();

        StorageImpl impl = null;

        switch (storageConfig.method()) {
            case MARIADB -> impl = new MariaDBImpl();
            case MYSQL -> impl = new MySQLImpl();
            case POSTGRESQL -> impl = new PostgreSQLImpl();
            case MONGODB -> impl = new MongoDBImpl();

            case H2 -> impl = new H2Impl();
            case SQLITE -> impl = new SQLiteImpl();

            case JSON -> impl = new JSONImpl();
            case YAML -> impl = new YAMLImpl();
            case CSV -> impl = new CSVImpl();
        }

        storage.implementation(impl);
        log.info("Using " + storage.name() + " as storage method.");

        storage.initialize(storageConfig);
    }

    @Override
    protected void cleanup() {
        if (storage == null || storage.implementation() == null) {
            return;
        }

        storage.close();
    }
}
