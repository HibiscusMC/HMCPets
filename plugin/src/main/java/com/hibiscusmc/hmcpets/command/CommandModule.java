package com.hibiscusmc.hmcpets.command;

import me.fixeddev.commandflow.annotated.CommandClass;
import team.unnamed.inject.AbstractModule;

public class CommandModule extends AbstractModule {
    @Override
    protected void configure() {
        multibind(CommandClass.class)
                .asSet()
                .to(PetsCommand.class)
                .to(PetsAdminCommand.class);
    }
}