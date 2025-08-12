package com.hibiscusmc.hmcpets.cache;

import team.unnamed.inject.AbstractModule;
import team.unnamed.inject.Provides;
import team.unnamed.inject.Singleton;

public class CacheModule extends AbstractModule {
    @Provides
    @Singleton
    public UserCache userCache() {
        return new UserCache();
    }
}
