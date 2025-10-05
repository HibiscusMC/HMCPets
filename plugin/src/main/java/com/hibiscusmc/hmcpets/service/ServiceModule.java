package com.hibiscusmc.hmcpets.service;

import com.hibiscusmc.hmcpets.command.CommandService;
import com.hibiscusmc.hmcpets.config.internal.ConfigService;
import com.hibiscusmc.hmcpets.listener.ListenerService;
import com.hibiscusmc.hmcpets.storage.StorageService;
import team.unnamed.inject.AbstractModule;

public class ServiceModule extends AbstractModule {

    @Override
    protected void configure() {
        multibind(Service.class)
                .asSet()
                .to(ConfigService.class)
                .to(CommandService.class)
                .to(ListenerService.class)
                .to(StorageService.class);
    }

}