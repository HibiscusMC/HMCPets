package com.hibiscusmc.hmcpets.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import com.hibiscusmc.hmcpets.api.HMCPets;
import com.hibiscusmc.hmcpets.cache.UserCache;
import com.hibiscusmc.hmcpets.config.MenuConfig;
import com.hibiscusmc.hmcpets.config.LangConfig;
import com.hibiscusmc.hmcpets.api.model.PetModel;
import com.hibiscusmc.hmcpets.api.model.UserModel;
import com.hibiscusmc.hmcpets.storage.StorageHolder;
import com.hibiscusmc.hmcpets.api.util.Adventure;
import lombok.extern.java.Log;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import team.unnamed.inject.Inject;

import java.util.Set;
import java.util.stream.Collectors;

@CommandAlias("hmcpets|pets|pet")
@Log(topic = "HMCPets")
public class PetsCommand extends BaseCommand {

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

    @Default
    @CommandAlias("list")
    @CommandPermission("hmcpets.commands.list")
    public void onListCommand(Player player) {
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

    @CommandAlias("version")
    @CommandPermission("hmcpets.commands.version")
    public void onVersionCommand(CommandSender sender) {
        String version = instance.getPluginMeta().getVersion();

        sender.sendMessage(Adventure.parse("version " + version));
    }

}