package com.hibiscusmc.hmcpets.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.hibiscusmc.hmcpets.api.HMCPets;
import com.hibiscusmc.hmcpets.api.model.PetModel;
import com.hibiscusmc.hmcpets.api.storage.Storage;
import com.hibiscusmc.hmcpets.cache.UserCache;
import com.hibiscusmc.hmcpets.config.LangConfig;
import com.hibiscusmc.hmcpets.config.MenuConfig;
import com.hibiscusmc.hmcpets.config.PetConfig;
import com.hibiscusmc.hmcpets.config.PluginConfig;
import com.hibiscusmc.hmcpets.pet.PetData;
import com.hibiscusmc.hmcpets.storage.StorageHolder;
import com.hibiscusmc.hmcpets.util.Debug;
import lombok.extern.java.Log;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import team.unnamed.inject.Inject;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

@CommandAlias(value = "hmcpetsadmin|petsadmin|petadmin")
@CommandPermission("hmcpets.admincommands")
@Log(topic = "HMCPets")
public class PetsAdminCommand extends BaseCommand {

    @Inject
    private HMCPets instance;
    @Inject
    private LangConfig langConfig;
    @Inject
    private PluginConfig pluginConfig;
    @Inject
    private MenuConfig menuConfig;
    @Inject
    private PetConfig petConfig;

    @Inject
    private UserCache userCache;
    @Inject
    private StorageHolder storage;

    @Subcommand("debug")
    @CommandPermission("hmcpets.admincommands.debug")
    public void debug(CommandSender sender) {
        boolean status = Debug.toggleDebug();

        langConfig.commandAdminDebug().send(
                sender,
                Map.of("status", status ?
                        langConfig.constantsEnabled().string() :
                        langConfig.constantsDisabled().string()
                )
        );
    }

    @Subcommand("add")
    @CommandPermission("hmcpets.admincommands.add")
    @CommandCompletion("@players @pets")
    public void add(CommandSender sender, OfflinePlayer player, String petName){
        if (player == null) {
            sender.sendRichMessage("<red>Player not found!");
            return;
        }

        userCache.fetch(player.getUniqueId()).thenAccept(user -> {
            Optional<PetData> data = petConfig.getPetData(petName);
            if(data.isEmpty()){
                sender.sendRichMessage("<red>Pet not found!");
                return;
            }

            user.addPet(PetModel.of(user, data.get()));
            sender.sendRichMessage("<green>Pet added!");
        });
    }

    @Subcommand("rename")
    @CommandPermission("hmcpets.admincommands.rename")
    public void rename(CommandSender sender, OfflinePlayer player, String petId, String newName) {
        if (player == null) {
            sender.sendRichMessage("<red>Player not found!");
            return;
        }

        Storage impl = storage.implementation();

        //TODO: Why is this using direct SQL access on a command? This is supposed to edit the cached version!
        /*userCache.fetch(player.getUniqueId()).thenAccept(user -> {
            if (user == null) {
                sender.sendRichMessage("<red>User not in the database!");
                return;
            }

            PetModel pet = impl.selectPet(user, petId);
            if (pet == null) {
                sender.sendRichMessage("<red>Pet not found!");
                return;
            }

            sender.sendRichMessage("Old pet name: " + pet.name());
            impl.updatePetName(pet, newName);
            sender.sendRichMessage("New pet name: " + pet.name());
        }).whenComplete((v, ex) -> {
            if (ex != null) {
                log.severe(ex.getMessage());
            }
        });*/
    }

    @Subcommand("listpets")
    @CommandPermission("hmcpets.admincommands.listpets")
    public void listPets(CommandSender sender, OfflinePlayer player) {
        if (player == null) {
            sender.sendRichMessage("<red>Player not found!");
            return;
        }

        Storage impl = storage.implementation();

        userCache.fetch(player.getUniqueId()).thenAccept(user -> {
            if (user == null) {
                sender.sendRichMessage("<red>User not in the database!");
                return;
            }

            Set<PetModel> pets = impl.selectPets(user);
            if (pets.isEmpty()) {
                sender.sendRichMessage("<red>No pets found!");
                return;
            }

            for (PetModel pet : pets) {
                sender.sendRichMessage("<gray>- " + pet.config().id() + " (id: " + pet.id() + ")<dark_gray>: <white>" + pet.name());
            }
        }).whenComplete((v, ex) -> {
            if (ex != null) {
                log.severe(ex.getMessage());
            }
        });
    }

    @Subcommand("rl|reload")
    @CommandPermission("hmcpets.admincommands.reload")
    @CommandCompletion("all|lang|config|pets|menus")
    public void reload(CommandSender sender, @Default("") String category) {
        long start = System.currentTimeMillis();

        switch (category) {
            case "lang":
                langConfig.setup();

                break;
            case "config":
                pluginConfig.setup();

                break;
            case "pets":
                petConfig.setup();

                break;
            case "menus":
                menuConfig.setup();

                break;
            default:
                category = "all";

                langConfig.setup();
                pluginConfig.setup();
                petConfig.setup();
                menuConfig.setup();

                break;
        }
        long end = System.currentTimeMillis();

        langConfig.commandAdminReload().send(sender, Map.of("type", category, "ms", (end - start) + ""));
    }


    @Subcommand("points")
    @CommandPermission("hmcpets.admincommands.points")
    public class PointsCommands extends BaseCommand{

        @Subcommand("add")
        @CommandCompletion("@players points")
        @CommandPermission("hmcpets.admincommands.points.add")
        public void addPoints(CommandSender sender, OfflinePlayer player, int points){
            userCache.fetch(player.getUniqueId()).thenAccept(user -> {
                user.petPoints(user.petPoints() + points);

                sender.sendMessage(points + " Pet Points added to player " + player.getName());
            });
        }

        @Subcommand("remove")
        @CommandCompletion("@players points")
        @CommandPermission("hmcpets.admincommands.points.remove")
        public void removePoints(CommandSender sender, OfflinePlayer player, int points){
            userCache.fetch(player.getUniqueId()).thenAccept(user -> {
               user.petPoints(user.petPoints() - points);

               if(!pluginConfig.users().allowNegativePetPointsBalance()){
                   user.petPoints(0);
               }

               sender.sendMessage(points + " Pet Points deducted from player " + player.getName());
            });
        }

    }

}