package com.hibiscusmc.hmcpets.i18n;

import com.hibiscusmc.hmcpets.config.AbstractConfig;
import lombok.Getter;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;

import java.nio.file.Path;
import java.util.Arrays;

@Getter
public class LangConfig extends AbstractConfig {
    private final LangEntry prefix
            = new LangEntry("<b><gradient:#d24c9f:#FF8282>HMCPets</gradient> <dark_gray>»</dark_gray></b> <gray>");
    private final LangEntry noPermission
            = new LangEntry(this, "<prefix><red>No permission.");

    private final LangEntry commandAdminReload
            = new LangEntry(this, "<prefix><gray>Reloaded <color:#d24c9f><type></color> files in <color:#d24c9f><ms>ms</color>!");
    private final LangEntry commandAdminDebug
            = new LangEntry(this, "<prefix><gray>Debug mode: <status>");

    private final LangEntry commandMainHelp
            = new LangEntry(this, "");

    private final LangEntry commandUsage
            = new LangEntry(this, "<prefix><gray>Usage: <#d24c9f>/<command> <usage>");

    private final LangEntry petsRarityCommon
            = new LangEntry("<gray><b>COMMON</b></gray>");
    private final LangEntry petsRarityRare
            = new LangEntry("<blue><b>RARE</b></blue>");
    private final LangEntry petsRarityEpic
            = new LangEntry("<dark_purple><b>EPIC</b></dark_purple>");
    private final LangEntry petsRarityLegendary
            = new LangEntry("<gold><b>LEGENDARY</b></gold>");

    private final LangEntry constantsEnabled
            = new LangEntry("<green><b>ENABLED</b></green>");
    private final LangEntry constantsDisabled
            = new LangEntry("<red><b>DISABLED</b></red>");

    public LangConfig(Path path) {
        super(path);
    }

    public void setup() {
        load();

        get("prefix", prefix);
        get("no-permission", noPermission);

        get("commands.admin.reload", commandAdminReload);
        get("commands.admin.debug", commandAdminDebug);

        get("commands.usage", commandUsage);

        get("pets.rarity.common", petsRarityCommon);
        get("pets.rarity.rare", petsRarityRare);
        get("pets.rarity.epic", petsRarityEpic);
        get("pets.rarity.legendary", petsRarityLegendary);

        get("constants.enabled", constantsEnabled);
        get("constants.disabled", constantsDisabled);
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