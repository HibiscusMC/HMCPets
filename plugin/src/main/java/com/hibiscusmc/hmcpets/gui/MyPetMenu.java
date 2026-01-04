package com.hibiscusmc.hmcpets.gui;

import com.hibiscusmc.hmcpets.api.data.IPetLevelData;
import com.hibiscusmc.hmcpets.api.gui.Button;
import com.hibiscusmc.hmcpets.api.model.PetModel;
import com.hibiscusmc.hmcpets.api.util.Adventure;
import com.hibiscusmc.hmcpets.config.LangConfig;
import com.hibiscusmc.hmcpets.config.MenuConfig;
import com.hibiscusmc.hmcpets.config.PluginConfig;
import com.hibiscusmc.hmcpets.config.internal.AbstractConfig;
import com.hibiscusmc.hmcpets.gui.internal.PetMenu;
import dev.triumphteam.gui.builder.item.ItemBuilder;
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
import java.util.*;

public class MyPetMenu extends AbstractConfig implements PetMenu {

    private String title;
    private int rows;

    private int petPreviewSlot;

    private Button levelMenuBtn, levelIndicator;

    private List<Integer> levelBtnSlots;
    private Map<Integer, ItemStack> expBars, hpBars;

    private List<Button> extraButtons;


    @Inject
    private LangConfig langConfig;
    @Inject
    private PluginConfig pluginConfig;
    @Inject
    public MenuConfig menuConfig;
    @Inject
    private Plugin instance;

    public MyPetMenu(Path path) {
        super(path);
    }


    @Override
    public void setup() {
        load();

        title = null;
        rows = 0;
        levelIndicator = null;
        levelMenuBtn = null;

        levelBtnSlots = new ArrayList<>();
        extraButtons = new ArrayList<>();

        expBars = new HashMap<>();
        hpBars = new HashMap<>();

        title = get("title").getString("All Pets");
        rows = get("rows").getInt(5);
        petPreviewSlot = get("pet-preview-slot").getInt(10);

        try {
            levelBtnSlots = get("levels-slots").getList(Integer.class);

            for (var entry : get("icons").childrenMap().entrySet()) {
                String iconKey = entry.getKey().toString();

                if(iconKey.startsWith("exp-")){
                    expBars.put(Integer.parseInt(iconKey.substring("exp-".length())), parse(get("icons." + iconKey + ".item").get(ItemStack.class)));
                }

                if(iconKey.startsWith("hp-")){
                    hpBars.put(Integer.parseInt(iconKey.substring("hp-".length())), parse(get("icons." + iconKey + ".item").get(ItemStack.class)));
                }

                switch (iconKey) {
                    case "levels-btn" -> {
                        ItemStack item = parse(get("icons.levels-btn.item").get(ItemStack.class));
                        int slot = get("icons.levels-btn.slot").getInt();

                        levelMenuBtn = Button.of(item, slot);
                    }

                    case "levels-indicator" -> {
                        ItemStack item = parse(get("icons.levels-indicator.item").get(ItemStack.class));
                        int slot = get("icons.levels-indicator.slot").getInt();

                        levelIndicator = Button.of(item, slot);
                    }

                    default -> {
                        ItemStack item = parse(get("icons." + iconKey + ".item").get(ItemStack.class));
                        int slot = get("icons." + iconKey + ".slot").getInt();
                        List<String> actions = get("icons." + iconKey + ".actions").getList(String.class);

                        extraButtons.add(Button.of(item, slot, actions));
                    }
                }
            }
        } catch (SerializationException | NullPointerException e) {
            throw new RuntimeException("Cannot parse my_pet.yml menu: " + e.getMessage());
        }
    }

    public void open(Player player, PetModel pet) {
        Gui gui = Gui.gui()
                .title(Adventure.parse(title))
                .rows(rows)
                .disableAllInteractions()
                .create();

        ItemStack petIcon = pet.config().rawIcon().clone();
        petIcon.editMeta(meta -> meta.customName(Component.text(pet.name())));

        //Pet Preview
        gui.setItem(petPreviewSlot, new GuiItem(petIcon));

        //Add all the interactable invisible buttons for the Levels Menu
        for(int slot : levelBtnSlots){
            gui.setItem(slot, new GuiItem(levelMenuBtn.item().clone(), event -> menuConfig.petLevelsMenu().open(player, 0)));
        }

        //Dynamically create the right bar for the pet level & assign it to the bar indicator
        gui.setItem(levelIndicator.slot(), new GuiItem(createLevelBarItem(pet), event -> menuConfig.petLevelsMenu().open(player, 0)));

        gui.open(player);
    }

    private ItemStack createLevelBarItem(PetModel pet){
        Optional<IPetLevelData> levelData = pet.config().getLevel(pet.level());
        if(levelData.isEmpty()) return levelMenuBtn.item().clone(); //Make this transparent if no level has been found, defaults to empty as per the underlying art

        int levelPercentage = Math.toIntExact(pet.experience() / levelData.get().expToNextLevel() * 10);

        ItemStack expBarItem = expBars.get(levelPercentage);
        if(expBarItem == null) expBarItem = levelMenuBtn.item().clone();

        return ItemBuilder.from(expBarItem)
                .name(levelIndicator.item().displayName())
                .lore(levelIndicator.item().lore() == null ? new ArrayList<>() : levelIndicator.item().lore())
                .build();
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