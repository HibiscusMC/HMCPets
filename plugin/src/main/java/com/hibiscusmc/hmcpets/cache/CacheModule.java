package com.hibiscusmc.hmcpets.cache;

import team.unnamed.inject.AbstractModule;

public class CacheModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(UserCache.class).to(UserCache.class);
    }

}