package com.hibiscusmc.hmcpets.gui;

import com.hibiscusmc.hmcpets.api.gui.Button;
import com.hibiscusmc.hmcpets.api.util.Adventure;
import com.hibiscusmc.hmcpets.config.LangConfig;
import com.hibiscusmc.hmcpets.config.PluginConfig;
import com.hibiscusmc.hmcpets.config.internal.AbstractConfig;
import com.hibiscusmc.hmcpets.gui.internal.PetMenu;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import me.lojosho.shaded.configurate.serialize.SerializationException;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;
import team.unnamed.inject.Inject;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class PetLevelsMenu extends AbstractConfig implements PetMenu {

    private String startingTitle, continueTitle, endTitle;
    private int rows;

    private int prevPageSlot, nextPageSlot;

    private List<Integer> levelSlots;

    private List<Button> extraButtons;

    @Inject
    private LangConfig langConfig;
    @Inject
    private PluginConfig pluginConfig;
    @Inject
    private Plugin instance;

    public PetLevelsMenu(Path path) {
        super(path);
    }


    @Override
    public void setup() {
        load();

        startingTitle = null;
        continueTitle = null;
        endTitle = null;
        rows = 0;

        levelSlots = new ArrayList<>();

        extraButtons = new ArrayList<>();

        startingTitle = get("starting-title").getString("Levels");
        continueTitle = get("continue-title").getString("Levels");
        endTitle = get("end-title").getString("Levels");

        rows = get("rows").getInt(5);

        prevPageSlot = get("prev-page-slot").getInt(10);
        nextPageSlot = get("next-page-slot").getInt(10);

        try {
            levelSlots = get("levels-slots").getList(Integer.class);

            for (var entry : get("icons").childrenMap().entrySet()) {
                String iconKey = entry.getKey().toString();

                switch (iconKey) {
                    default: {
                        ItemStack item = parse(get("icons." + iconKey + ".item").get(ItemStack.class));
                        int slot = get("icons." + iconKey + ".slot").getInt();
                        List<String> actions = get("icons." + iconKey + ".actions").getList(String.class);

                        extraButtons.add(Button.of(item, slot, actions));
                        break;
                    }
                }
            }
        } catch (SerializationException | NullPointerException e) {
            throw new RuntimeException("Cannot parse pet_levels.yml menu: " + e.getMessage());
        }
    }

    public void open(Player player, int page) {
        Gui gui = Gui.gui()
                .title(Adventure.parse(startingTitle))
                .rows(rows)
                .disableAllInteractions()
                .create();

        //gui.setItem(prevPageSlot, new GuiItem(pet.config().rawIcon()));

        for (Button button : extraButtons) {
            gui.setItem(button.slot(), new GuiItem(button.item(), event -> button.runActions((Player) event.getWhoClicked())));
        }

        gui.open(player);
    }


    private ItemStack parse(@Nullable ItemStack stack) {
        if (stack == null) {
            return null;
        }

        stack.editMeta(meta -> {
            meta.customName(Adventure.parseForMeta(Adventure.unparse(meta.customName())));

            List<Component> lore = meta.lore();
            if (lore != null) {
                meta.lore(lore.stream().map(line ->
                        Adventure.parseForMeta(Adventure.unparse(line))
                ).toList());
            }
        });

        return stack;
    }
}