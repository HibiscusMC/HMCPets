package com.hibiscusmc.hmcpets.command;

import com.hibiscusmc.hmcpets.api.HMCPets;
import com.hibiscusmc.hmcpets.cache.UserCache;
import com.hibiscusmc.hmcpets.config.MenuConfig;
import com.hibiscusmc.hmcpets.config.LangConfig;
import com.hibiscusmc.hmcpets.api.model.PetModel;
import com.hibiscusmc.hmcpets.api.model.UserModel;
import com.hibiscusmc.hmcpets.storage.StorageHolder;
import com.hibiscusmc.hmcpets.api.storage.Storage;
import com.hibiscusmc.hmcpets.api.util.Adventure;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import me.fixeddev.commandflow.annotated.annotation.Usage;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import team.unnamed.inject.Inject;

import java.util.List;

@Command(
        names = {"hmcpets", "pets", "pet"},
        desc = "Main commands for HMCPets related things",
        permission = "hmcpets.commands"
)
@Usage(value = "[help | list]")
public class PetsCommand implements CommandClass {

    @Inject
    private HMCPets instance;

    @Inject
    private MenuConfig menuConfig;
    @Inject
    private LangConfig langConfig;

    @Inject
    private UserCache userCache;
    @Inject
    private StorageHolder storage;

    @Command(names = {"", "help"}, permission = "hmcpets.commands")
    public void onMainCommand(CommandSender sender) {
        String version = instance.getPluginMeta().getVersion();

        sender.sendMessage(Adventure.parse("version " + version));
    }

    @Command(names = {"list", "menu"}, permission = "hmcpets.commands.list", desc = "list [player]")
    public void onListCommand(Player sender) {
        UserModel user = userCache.fetch(sender.getUniqueId());
        if (user == null) {
            langConfig.constantsNoPets().send(sender);
            return;
        }

        Storage impl = storage.implementation();

        List<PetModel> pets = impl.selectPets(user);
        if (pets.isEmpty()) {
            langConfig.constantsNoPets().send(sender);
            return;
        }

        menuConfig.listPetsMenu().open(sender, user, pets);
    }

}
