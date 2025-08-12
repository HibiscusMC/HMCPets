package com.hibiscusmc.hmcpets.command;

import com.hibiscusmc.hmcpets.command.impl.CommandArgumentImpl;
import com.hibiscusmc.hmcpets.i18n.LangConfig;
import com.hibiscusmc.hmcpets.service.Service;
import me.fixeddev.commandflow.CommandManager;
import me.fixeddev.commandflow.annotated.AnnotatedCommandTreeBuilder;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.part.PartInjector;
import me.fixeddev.commandflow.annotated.part.defaults.DefaultsModule;
import me.fixeddev.commandflow.bukkit.BukkitCommandManager;
import me.fixeddev.commandflow.bukkit.factory.BukkitModule;
import me.fixeddev.commandflow.command.Command;
import me.fixeddev.commandflow.exception.CommandUsage;
import me.fixeddev.commandflow.exception.NoPermissionsException;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.command.CommandSender;
import team.unnamed.inject.Inject;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class CommandService extends Service {
    @Inject
    private Set<CommandClass> commandClasses;
    @Inject
    private LangConfig langConfig;

    protected CommandService() {
        super("Command");
    }

    @Override
    protected void initialize() {
        PartInjector injector = PartInjector.create();
        injector.install(new DefaultsModule());
        injector.install(new BukkitModule());

        injector.install(new CommandArgumentImpl());

        AnnotatedCommandTreeBuilder treeBuilder = AnnotatedCommandTreeBuilder
                .create(injector);

        CommandManager manager = new BukkitCommandManager("hmcpets");

        manager.getErrorHandler().addExceptionHandler(NoPermissionsException.class, (namespace, throwable) -> {
            CommandSender sender = namespace.getObject(CommandSender.class, "SENDER");

            langConfig.noPermission()
                    .send(sender);
            return true;
        });

        PlainTextComponentSerializer plainText = PlainTextComponentSerializer.plainText();

        manager.getErrorHandler().addExceptionHandler(CommandUsage.class, (namespace, exx) -> {
            CommandSender sender = namespace.getObject(CommandSender.class, "SENDER");
            String label = namespace.getObject(String.class, "label");
            Command command = exx.getCommand();

            langConfig.commandUsage()
                    .send(sender, Map.of(
                            "command", label,
                            "usage", plainText.serialize(Objects.requireNonNull(command.getUsage()))
                    ));
            return true;
        });

        for (CommandClass commandClass : commandClasses) {
            manager.registerCommands(treeBuilder.fromClass(commandClass));
        }
    }

    @Override
    protected void cleanup() {

    }
}
