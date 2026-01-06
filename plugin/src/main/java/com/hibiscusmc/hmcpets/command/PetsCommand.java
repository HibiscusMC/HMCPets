package com.hibiscusmc.hmcpets.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import com.hibiscusmc.hmcpets.api.HMCPets;
import com.hibiscusmc.hmcpets.api.model.PetModel;
import com.hibiscusmc.hmcpets.api.model.UserModel;
import com.hibiscusmc.hmcpets.api.util.Adventure;
import com.hibiscusmc.hmcpets.cache.UserCache;
import com.hibiscusmc.hmcpets.config.LangConfig;
import com.hibiscusmc.hmcpets.config.MenuConfig;
import com.hibiscusmc.hmcpets.storage.StorageHolder;
import lombok.extern.java.Log;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import team.unnamed.inject.Inject;

import java.util.Optional;
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
    @Subcommand("list")
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


    @Subcommand("sell")
    @CommandPermission("hmcpets.commands.sell")
    public void onPetSellCommand(Player player, String petID){
        userCache.fetch(player.getUniqueId()).thenAccept(user -> {
            Optional<PetModel> pet = user.getPet(petID);
            if(pet.isEmpty()){
                player.sendMessage(Component.text("There is no pet with that ID!"));
                return;
            }

            if(pet.get().isObtainedViaPerms()){
                player.sendMessage(Component.text("You cannot sell this pet!"));
                return;
            }

            user.petPoints(user.petPoints() + pet.get().config().petPoints());
            user.removePet(pet.get());
            player.sendMessage(Component.text("Pet sold for " + pet.get().config().petPoints() + " points!"));
        });
    }

    @Subcommand("version")
    @CommandPermission("hmcpets.commands.version")
    public void onVersionCommand(CommandSender sender) {
        String version = instance.getPluginMeta().getVersion();

        sender.sendMessage(Adventure.parse("version " + version));
    }

}