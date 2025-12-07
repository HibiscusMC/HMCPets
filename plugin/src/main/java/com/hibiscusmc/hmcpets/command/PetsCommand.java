package com.hibiscusmc.hmcpets.command;

import com.hibiscusmc.hmcpets.api.HMCPets;
import com.hibiscusmc.hmcpets.cache.UserCache;
import com.hibiscusmc.hmcpets.config.MenuConfig;
import com.hibiscusmc.hmcpets.config.LangConfig;
import com.hibiscusmc.hmcpets.api.model.PetModel;
import com.hibiscusmc.hmcpets.api.model.UserModel;
import com.hibiscusmc.hmcpets.storage.StorageHolder;
import com.hibiscusmc.hmcpets.api.util.Adventure;
import lombok.extern.java.Log;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.ArgOrSub;
import me.fixeddev.commandflow.annotated.annotation.Command;
import me.fixeddev.commandflow.annotated.annotation.Usage;
import me.fixeddev.commandflow.bukkit.annotation.Sender;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import team.unnamed.inject.Inject;

import java.util.Set;
import java.util.stream.Collectors;

@Command(
        names = {"hmcpets", "pets", "pet"},
        desc = "Main commands for HMCPets related things",
        permission = "hmcpets.commands"
)
@Usage(value = "[version | list]")
@ArgOrSub(value = true)
@Log(topic = "HMCPets")
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

    @Command(names = {"", "list"}, permission = "hmcpets.commands.list")
    @Usage(value = "[player]")
    public void onListCommand(@Sender Player player) {
        new Location()

        userCache.fetch(player.getUniqueId()).thenAccept(user -> {
            if (user == null) {
                langConfig.constantsNoPets().send(player);
                return;
            }

            Set<PetModel> pets = user.pets().values().stream().map(UserModel.CachedPet::pet).collect(Collectors.toSet());
            if (pets.isEmpty()) {
                langConfig.constantsNoPets().send(player);
                return;
            }

            Bukkit.getScheduler().runTask(instance, () -> menuConfig.listPetsMenu().open(player, user, pets));
        }).whenComplete((v, ex) -> {
            if (ex != null) {
                log.severe(ex.getMessage());
            }
        });
    }

    @Command(names = {"version"}, permission = "hmcpets.commands.version")
    public void onVersionCommand(CommandSender sender) {
        String version = instance.getPluginMeta().getVersion();

        sender.sendMessage(Adventure.parse("version " + version));
    }

}