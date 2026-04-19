package com.hibiscusmc.hmcpets.config;

import com.hibiscusmc.hmcpets.api.data.ILangData;
import com.hibiscusmc.hmcpets.api.i18n.LangEntry;
import com.hibiscusmc.hmcpets.config.internal.AbstractConfig;
import lombok.Getter;
import me.lojosho.shaded.configurate.CommentedConfigurationNode;
import me.lojosho.shaded.configurate.ConfigurateException;
import org.bukkit.plugin.Plugin;
import team.unnamed.inject.Inject;
import team.unnamed.inject.Singleton;

import java.nio.file.Path;
import java.util.Arrays;

@Getter
@Singleton
public class LangConfig extends AbstractConfig implements ILangData {

    private final LangEntry prefix = new LangEntry("<b><gradient:#d24c9f:#FF8282>HMCPets</gradient> <dark_gray>»</dark_gray></b> <gray>");
    private final LangEntry noPermission = new LangEntry("<prefix><red>No permission.");
    private final LangEntry playersOnlyCommand = new LangEntry("<prefix><red>This command can only be executed by a player.");

    private final LangEntry commandAdminReload = new LangEntry("<prefix><gray>Reloaded <color:#d24c9f><type></color> files in <color:#d24c9f><ms>ms</color>!");
    private final LangEntry commandAdminDebug = new LangEntry("<prefix><gray>Debug mode: <status>");


    private final LangEntry guisPetNotSpawned = new LangEntry("<prefix><red>Your Pet is not spawned! Spawn it first to be able to modify it!");
    private final LangEntry guisHpBarName = new LangEntry("<white>HP: <hp>/<maxhp>");
    private final LangEntry guisHungerBarName = new LangEntry("<white>Hunger: <hunger>/<maxhunger>");
    private final LangEntry guisRenamePet = new LangEntry("<prefix><gray>Enter a new name for your pet (write 'cancel' to cancel):");
    private final LangEntry guisRenameCanceled = new LangEntry("<prefix><gray>Rename canceled.");
    private final LangEntry guisRenameDone = new LangEntry("<prefix><gray>Your pet's name has been changed to <pet>.");
    private final LangEntry guisLevelStatusLocked = new LangEntry("<red><bold>Locked");
    private final LangEntry guisLevelStatusUnlocked = new LangEntry("<green><bold>Unlocked");
    private final LangEntry guisLevelStatusCurrent = new LangEntry("<yellow><bold>Current");
    private final LangEntry guisEquipped = new LangEntry("<green>Equipped!");


    private final LangEntry petsMaxActive = new LangEntry("<prefix><red>You can't have more active pets!");

    private final LangEntry petsRarityCommon = new LangEntry("<gray><b>COMMON</b></gray>");
    private final LangEntry petsRarityRare = new LangEntry("<blue><b>RARE</b></blue>");
    private final LangEntry petsRarityEpic = new LangEntry("<dark_purple><b>EPIC</b></dark_purple>");
    private final LangEntry petsRarityLegendary = new LangEntry("<gold><b>LEGENDARY</b></gold>");

    private final LangEntry petsTypeAquatic = new LangEntry("<blue><b>Aquatic</b></blue>");
    private final LangEntry petsTypeBeast = new LangEntry("<red><b>Beast</b></red>");
    private final LangEntry petsTypeMagic = new LangEntry("<dark_purple><b>Magic</b></dark_purple>");
    private final LangEntry petsTypeCritter = new LangEntry("<green><b>Critter</b></green>");

    private final LangEntry petsLeveledUpTitle = new LangEntry("<green><b>LEVELED UP!</b></green>");
    private final LangEntry petsLeveledUpSubtitle = new LangEntry("<gray><pet> leveled up to Level <level>!");
    private final LangEntry petsDowned = new LangEntry("<prefix><red>Your pet, <dark_red><pet></dark_red>, has been downed! <gray>(<reason>)");
    private final LangEntry petsNametag = new LangEntry("<white><bold><pet></bold></white><newline><newline>Test");

    private final LangEntry constantsNoPets = new LangEntry("<prefix><red>You don't have any pets!");
    private final LangEntry constantsEnabled = new LangEntry("<green><b>ENABLED</b></green>");
    private final LangEntry constantsDisabled = new LangEntry("<red><b>DISABLED</b></red>");

    private final LangEntry constantsCurrentActive = new LangEntry("<green>");
    private final LangEntry constantsCurrentInactive = new LangEntry("<gray>");

    @Inject
    private Plugin plugin;

    public LangConfig(Path path) {
        super(path);
    }

    public void setup() {
        load();

        get("prefix", prefix);
        get("no-permission", noPermission);


        get("guis.hp-bar-name", guisHpBarName);
        get("guis.hunger-bar-name", guisHungerBarName);
        get("guis.rename-pet", guisRenamePet);
        get("guis.rename-canceled", guisRenameCanceled);
        get("guis.rename-done", guisRenameDone);
        get("guis.level-status-locked", guisLevelStatusLocked);
        get("guis.level-status-unlocked", guisLevelStatusUnlocked);
        get("guis.level-status-current", guisLevelStatusCurrent);
        get("guis.equipped-pet", guisEquipped);


        get("pets.max-active", petsMaxActive);

        get("pets.rarity.common", petsRarityCommon);
        get("pets.rarity.rare", petsRarityRare);
        get("pets.rarity.epic", petsRarityEpic);
        get("pets.rarity.legendary", petsRarityLegendary);

        get("pets.type.aquatic", petsTypeAquatic);
        get("pets.type.beast", petsTypeBeast);
        get("pets.type.magic", petsTypeMagic);
        get("pets.type.critter", petsTypeCritter);

        get("pets.leveled-up-title", petsLeveledUpTitle);
        get("pets.leveled-up-subtitle", petsLeveledUpSubtitle);
        get("pets.downed", petsDowned);
        get("pets.nametag", petsNametag);


        get("constants.no-pets", constantsNoPets);
        get("constants.enabled", constantsEnabled);
        get("constants.disabled", constantsDisabled);
        get("constants.current.active", constantsCurrentActive);
        get("constants.current.inactive", constantsCurrentInactive);
    }

    private void get(String path, LangEntry entry) {
        CommentedConfigurationNode node = configNode.node(Arrays.stream(path.split("\\.")).toList());

        String value = node.getString();
        if (value == null) {
            try {
                node.set(String.class, entry.string());
                loader.save(configNode);
            } catch (ConfigurateException e) {
                throw new RuntimeException(e);
            }
        } else {
            entry.string(value);
        }
    }
}