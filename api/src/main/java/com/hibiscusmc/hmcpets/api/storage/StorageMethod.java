package com.hibiscusmc.hmcpets.api.storage;

public enum StorageMethod {

    MARIADB(StorageMethodType.REMOTE, 3306),
    MYSQL(StorageMethodType.REMOTE, 3306),
    POSTGRESQL(StorageMethodType.REMOTE, 5432),
    MONGODB(StorageMethodType.REMOTE, 27017),

    H2(StorageMethodType.LOCAL),
    SQLITE(StorageMethodType.LOCAL),

    JSON(StorageMethodType.TEXT),
    YAML(StorageMethodType.TEXT),
    CSV(StorageMethodType.TEXT);

    private final StorageMethodType type;
    private final int port;

    StorageMethod(StorageMethodType type, int port) {
        this.type = type;
        this.port = port;
    }

    StorageMethod(StorageMethodType type) {
        this(type, -1);
    }

    public StorageMethodType type() {
        return type;
    }

    public int port() {
        return port;
    }

    public static StorageMethod fromString(String method) {
        try {
            return StorageMethod.valueOf(method.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

}