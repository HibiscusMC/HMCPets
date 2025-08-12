package com.hibiscusmc.hmcpets.storage;

import com.hibiscusmc.hmcpets.config.PluginConfig;
import com.hibiscusmc.hmcpets.service.Service;
import com.hibiscusmc.hmcpets.storage.impl.Storage;
import com.hibiscusmc.hmcpets.storage.impl.local.H2Storage;
import com.hibiscusmc.hmcpets.storage.impl.local.SQLiteStorage;
import com.hibiscusmc.hmcpets.storage.impl.remote.MariaDBStorage;
import com.hibiscusmc.hmcpets.storage.impl.remote.MongoDBStorage;
import com.hibiscusmc.hmcpets.storage.impl.remote.MySQLStorage;
import com.hibiscusmc.hmcpets.storage.impl.remote.PostgreSQLStorage;
import com.hibiscusmc.hmcpets.storage.impl.text.CSVStorage;
import com.hibiscusmc.hmcpets.storage.impl.text.JSONStorage;
import com.hibiscusmc.hmcpets.storage.impl.text.YAMLStorage;
import lombok.extern.java.Log;
import team.unnamed.inject.Inject;
import team.unnamed.inject.Injector;

@Log(topic = "HMCPets")
public class StorageService extends Service {

    @Inject
    private PluginConfig pluginConfig;
    @Inject
    private StorageHolder storage;
    @Inject
    private Injector injector;

    protected StorageService() {
        super("Storage");
    }

    @Override
    protected void initialize() {
        PluginConfig.StorageConfig storageConfig = pluginConfig.storage();

        Storage impl = null;

        switch (storageConfig.method()) {
            case MARIADB -> impl = injector.getInstance(MariaDBStorage.class);
            case MYSQL -> impl = injector.getInstance(MySQLStorage.class);
            case POSTGRESQL -> impl = injector.getInstance(PostgreSQLStorage.class);
            case MONGODB -> impl = injector.getInstance(MongoDBStorage.class);

            case H2 -> impl = injector.getInstance(H2Storage.class);
            case SQLITE -> impl = injector.getInstance(SQLiteStorage.class);

            case JSON -> impl = injector.getInstance(JSONStorage.class);
            case YAML -> impl = injector.getInstance(YAMLStorage.class);
            case CSV -> impl = injector.getInstance(CSVStorage.class);
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