package com.hibiscusmc.hmcpets.command;

import com.hibiscusmc.hmcpets.api.HMCPets;
import com.hibiscusmc.hmcpets.api.model.registry.PetRarity;
import com.hibiscusmc.hmcpets.cache.UserCache;
import com.hibiscusmc.hmcpets.config.PluginConfig;
import com.hibiscusmc.hmcpets.config.MenuConfig;
import com.hibiscusmc.hmcpets.config.LangConfig;
import com.hibiscusmc.hmcpets.api.model.PetModel;
import com.hibiscusmc.hmcpets.api.model.UserModel;
import com.hibiscusmc.hmcpets.config.PetConfig;
import com.hibiscusmc.hmcpets.storage.StorageHolder;
import com.hibiscusmc.hmcpets.api.storage.Storage;
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
@Usage(value = "[reload | debug | rename | listpets]")
public class PetsAdminCommand implements CommandClass {

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

    @Command(names = {"debug"}, permission = "hmcpets.admincommands.debug")
    public void onDebugCommand(CommandSender sender) {
        boolean status = Debug.toggleDebug();

        for (PetRarity rarity : instance.petRarityRegistry().getAllRegistered()) {
            System.out.println(rarity);
        }

        langConfig.commandAdminDebug().send(
                sender,
                Map.of("status", status ?
                        langConfig.constantsEnabled().string() :
                        langConfig.constantsDisabled().string()
                )
        );
    }

    @Command(names = {"rename"}, permission = "hmcpets.admincommands.rename")
    @Usage(value = "<player> <pet> <new name>")
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

        Storage impl = storage.implementation();

        UserModel user = userCache.fetch(player.getUniqueId());

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
    }

    @Command(names = {"listpets"}, permission = "hmcpets.admincommands.listpets")
    @Usage(value = "<player>")
    public void onListPetsCommand(CommandSender sender, OfflinePlayer player) {
        if (player == null) {
            sender.sendRichMessage("<red>Player not found!");
            return;
        }

        Storage impl = storage.implementation();

        UserModel user = userCache.fetch(player.getUniqueId());

        if (user == null) {
            user = new UserModel(-1, player.getUniqueId());
            impl.insertUser(user);
            sender.sendRichMessage("<red>User not in the database!");
            return;
        }

        List<PetModel> pets = impl.selectPets(user);
        if (pets.isEmpty()) {
            PetModel pet = new PetModel(-1, user, petConfig.allPets().get("kitty"));

            pet.name("<#d24c9f>Kitty Pet");
            pet.level(1);
            pet.experience(0);
            pet.rarity(PetRarity.COMMON);
            pet.craving(Hooks.getItem("PAPER"));
            pet.obtainedTimestamp(System.currentTimeMillis());

            impl.insertPet(pet);
            sender.sendRichMessage("<red>No pets found!");
            return;
        }

        for (PetModel pet : pets) {
            sender.sendRichMessage("<gray>- " + pet.config().id() + " (id: " + pet.id() + ")<dark_gray>: <white>" + pet.name());
        }
    }

    @Command(names = {"rl", "reload"}, permission = "hmcpets.admincommands.reload")
    @Usage(value = "[all | lang | config | pets]")
    public void onReloadCommand(
            CommandSender sender,
            @OptArg(value = "all")
            @Suggestions(suggestions = {"", "all", "lang", "config", "pets", "menus"})
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

}