package com.hibiscusmc.hmcpets.command;

import com.hibiscusmc.hmcpets.HMCPetsPlugin;
import com.hibiscusmc.hmcpets.util.Adventure;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import me.fixeddev.commandflow.annotated.annotation.Usage;
import org.bukkit.command.CommandSender;
import team.unnamed.inject.Inject;

@Command(
        names = {"hmcpets", "pets", "pet"},
        desc = "Main commands for HMCPets related things",
        permission = "hmcpets.commands"
)
@Usage(value = "[help | pets]")
public class PetsCommand implements CommandClass {
    @Inject
    private HMCPetsPlugin instance;

    @Command(names = {"", "help"}, permission = "hmcpets.commands")
    public void onMainCommand(CommandSender sender) {
        String version = instance.getPluginMeta().getVersion();

        sender.sendMessage(Adventure.parse("version " + version));
    }

    @Command(names = {"list", "menu"}, permission = "hmcpets.commands.list")
    public void onListCommand(CommandSender sender) {

    }
}
