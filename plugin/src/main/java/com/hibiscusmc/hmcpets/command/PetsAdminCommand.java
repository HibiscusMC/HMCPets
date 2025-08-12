package com.hibiscusmc.hmcpets.command;

import com.hibiscusmc.hmcpets.HMCPetsPlugin;
import com.hibiscusmc.hmcpets.cache.UserCache;
import com.hibiscusmc.hmcpets.config.PluginConfig;
import com.hibiscusmc.hmcpets.i18n.LangConfig;
import com.hibiscusmc.hmcpets.model.Pet;
import com.hibiscusmc.hmcpets.model.User;
import com.hibiscusmc.hmcpets.pet.PetConfig;
import com.hibiscusmc.hmcpets.storage.Storage;
import com.hibiscusmc.hmcpets.storage.impl.StorageImpl;
import com.hibiscusmc.hmcpets.util.Debug;
import me.fixeddev.commandflow.annotated.CommandClass;
import me.fixeddev.commandflow.annotated.annotation.Command;
import me.fixeddev.commandflow.annotated.annotation.OptArg;
import me.fixeddev.commandflow.annotated.annotation.Suggestions;
import me.fixeddev.commandflow.annotated.annotation.Text;
import me.fixeddev.commandflow.annotated.annotation.Usage;
import me.lojosho.hibiscuscommons.hooks.Hooks;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import team.unnamed.inject.Inject;

import java.util.List;
import java.util.Map;

@Command(
        names = {"hmcpetsadmin", "petsadmin", "petadmin"},
        desc = "Admin commands for HMCPets related things",
        permission = "hmcpets.admincommands")
@Usage(value = "[reload]")
public class PetsAdminCommand implements CommandClass {
    @Inject
    private HMCPetsPlugin instance;
    @Inject
    private LangConfig langConfig;
    @Inject
    private PluginConfig pluginConfig;
    @Inject
    private PetConfig petConfig;

    @Inject
    private UserCache userCache;
    @Inject
    private Storage storage;

    @Command(names = {"debug"}, permission = "hmcpets.admincommands.debug")
    public void onDebugCommand(CommandSender sender) {
        boolean status = Debug.toggleDebug();

        langConfig.commandAdminDebug().send(
                sender,
                Map.of("status", status ?
                        langConfig.constantsEnabled().string() :
                        langConfig.constantsDisabled().string()
                )
        );
    }

    @Command(names = {"rename"}, permission = "hmcpets.admincommands.rename")
    public void onRenameCommand(
            CommandSender sender,
            OfflinePlayer player,
            int petId,
            @Text String newName
    ) {
        if (player == null) {
            sender.sendRichMessage("<red>Player not found!");
            return;
        }

        StorageImpl impl = storage.implementation();

        User user = userCache.fetch(player.getUniqueId());

        if (user == null) {
            sender.sendRichMessage("<red>User not in the database!");
            return;
        }

        Pet pet = impl.selectPet(user, petId);
        if (pet == null) {
            sender.sendRichMessage("<red>Pet not found!");
            return;
        }

        sender.sendRichMessage("Old pet name: " + pet.name());
        impl.updatePetName(pet, newName);
        sender.sendRichMessage("New pet name: " + pet.name());
    }

    @Command(names = {"listpets"}, permission = "hmcpets.admincommands.listpets")
    public void onListPetsCommand(CommandSender sender, OfflinePlayer player) {
        if (player == null) {
            sender.sendRichMessage("<red>Player not found!");
            return;
        }

        StorageImpl impl = storage.implementation();

        User user = userCache.fetch(player.getUniqueId());

        if (user == null) {
            user = new User(-1, player.getUniqueId());
            impl.insertUser(user);
            sender.sendRichMessage("<red>User not in the database!");
            return;
        }

        List<Pet> pets = impl.selectPets(user);
        if (pets.isEmpty()) {
            Pet pet = new Pet(-1, user, petConfig.allPets().get("kitty"));

            pet.name("<#d24c9f>Kitty Pet");
            pet.level(1);
            pet.experience(0);
            pet.rarity(Pet.Rarity.COMMON);
            pet.craving(Hooks.getItem("PAPER"));
            pet.obtainedTimestamp(System.currentTimeMillis());

            impl.insertPet(pet);
            sender.sendRichMessage("<red>No pets found!");
            return;
        }

        for (Pet pet : pets) {
            sender.sendRichMessage("<gray>- " + pet.config().id() + " (id: " + pet.id() + ")<dark_gray>: <white>" + pet.name());
        }
    }

    @Command(names = {"rl", "reload"}, permission = "hmcpets.admincommands.reload")
    public void onReloadCommand(
            CommandSender sender,
            @OptArg(value = "all")
            @Suggestions(suggestions = {"", "all", "lang", "config", "pets"})
            String category
    ) {
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
            default:
                category = "all";

                langConfig.setup();
                pluginConfig.setup();
                petConfig.setup();

                break;
        }
        long end = System.currentTimeMillis();

        langConfig.commandAdminReload().send(sender, Map.of("type", category, "ms", (end - start) + ""));
    }

}
