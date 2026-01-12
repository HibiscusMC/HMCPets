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
import com.hibiscusmc.hmcpets.listener.ListenerService;
import com.hibiscusmc.hmcpets.listener.PetRenameChatListener;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import me.lojosho.shaded.configurate.serialize.SerializationException;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
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

    private Button renameBtn;

    private ItemStack levelMenuBtn, levelIndicator, hpIndicator, hungerIndicator;
    private int levelIndicatorSlot, hpIndicatorSlot, hungerIndicatorSlot;

    private List<Integer> levelBtnSlots;
    private Map<Integer, ItemStack> expBars, hpBars, hungerBars;

    private List<Button> extraButtons;


    @Inject
    private LangConfig langConfig;
    @Inject
    private PluginConfig pluginConfig;
    @Inject
    public MenuConfig menuConfig;
    @Inject
    private Plugin instance;

    private ListenerService listenerService;

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
        renameBtn = null;

        levelBtnSlots = new ArrayList<>();
        extraButtons = new ArrayList<>();
        hungerBars = new HashMap<>();

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
                    System.out.println("Added EXP bar n. " + Integer.parseInt(iconKey.substring("exp-".length())));
                    continue;
                }

                if(iconKey.startsWith("hp-")){
                    hpBars.put(Integer.parseInt(iconKey.substring("hp-".length())), parse(get("icons." + iconKey + ".item").get(ItemStack.class)));
                    continue;
                }

                if(iconKey.startsWith("hunger-")){
                    hungerBars.put(Integer.parseInt(iconKey.substring("hunger-".length())), parse(get("icons." + iconKey + ".item").get(ItemStack.class)));
                    continue;
                }

                switch (iconKey) {
                    case "rename-btn" -> {
                        renameBtn = Button.of(parse(get("icons.rename-btn.item").get(ItemStack.class)), get("icons.rename-btn.slot").getInt());
                    }

                    case "levels-btn" -> {
                        levelMenuBtn = parse(get("icons.levels-btn.item").get(ItemStack.class));
                    }

                    case "levels-indicator" -> {
                        levelIndicator = parse(get("icons.levels-indicator.item").get(ItemStack.class));
                        levelIndicatorSlot = get("icons.levels-indicator.slot").getInt();
                    }

                    case "food-indicator" -> {
                        hungerIndicator = parse(get("icons.food-indicator.item").get(ItemStack.class));
                        hungerIndicatorSlot = get("icons.food-indicator.slot").getInt();
                    }

                    case "health-indicator" -> {
                        hpIndicator = parse(get("icons.health-indicator.item").get(ItemStack.class));
                        hpIndicatorSlot = get("icons.health-indicator.slot").getInt();
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
            gui.setItem(slot, new GuiItem(levelMenuBtn.clone(), event -> menuConfig.petLevelsMenu().open(player, 0)));
        }

        //Dynamically create the right bar for the pet level & assign it to the bar indicator
        gui.setItem(levelIndicatorSlot, new GuiItem(createLevelBarItem(pet), event -> menuConfig.petLevelsMenu().open(player, 0)));

        gui.setItem(renameBtn.slot(), new GuiItem(renameBtn.item(), event -> renamePressed(player, pet)));

        gui.setItem(hungerIndicatorSlot, new GuiItem(createHungerBarItem(pet), event -> menuConfig.petLevelsMenu().open(player, 0)));
        gui.setItem(hpIndicatorSlot, new GuiItem(createHealthBarItem(pet), event -> menuConfig.petLevelsMenu().open(player, 0)));

        extraButtons.forEach(button -> gui.setItem(button.slot(), new GuiItem(button.item(), event -> button.runActions(player))));

        gui.open(player);
    }

    //Create EXP bar display
    private ItemStack createLevelBarItem(PetModel pet){
        Optional<IPetLevelData> levelData = pet.getNextLevelData();
        if(levelData.isEmpty()) return levelMenuBtn.clone(); //Make this transparent if no level has been found, defaults to empty as per the underlying art

        int levelPercentage = levelData.get().expRequired() == 0 ? 0 : (int)Math.floor((double)pet.experience() / (double)levelData.get().expRequired() * 10D);

        ItemStack expBarItem = expBars.get(levelPercentage);
        if(expBarItem == null) expBarItem = levelMenuBtn;

        expBarItem = expBarItem.clone();
        expBarItem.editMeta(meta -> {
           meta.customName(levelIndicator.effectiveName());
           if(levelIndicator.lore() != null) meta.lore(levelIndicator.lore());
        });

        return expBarItem;
    }

    //Create HP bar display
    private ItemStack createHealthBarItem(PetModel pet){
        System.out.println("a");
        Optional<IPetLevelData> levelData = pet.getLevelData();
        if(levelData.isEmpty()) return new ItemStack(Material.AIR); //Make this transparent if no level has been found, defaults to empty as per the underlying art
        System.out.println("b");
        int hpPercentage = levelData.get().maxHealth() == 0 ? 0 : (int)Math.floor((double)pet.health() / (double)levelData.get().maxHealth() * 6D);
        System.out.println(hpPercentage + " hp (" + pet.health() + "/" + levelData.get().maxHealth() + ")");
        ItemStack hpBarItem = hpBars.get(hpPercentage);
        if(hpBarItem == null) hpBarItem = new ItemStack(Material.AIR);

        System.out.println("c");
        hpBarItem = hpBarItem.clone();
        hpBarItem.editMeta(meta -> {
           meta.displayName(Component.text("HP: " + pet.health() + "/" + levelData.get().maxHealth()));
        });

        return hpBarItem;
    }

    //Create hunger bar display
    private ItemStack createHungerBarItem(PetModel pet){
        Optional<IPetLevelData> levelData = pet.getLevelData();
        if(levelData.isEmpty()) return new ItemStack(Material.AIR); //Make this transparent if no level has been found, defaults to empty as per the underlying art

        int hpPercentage = levelData.get().maxHunger() == 0 ? 0 : (int)Math.floor((double)pet.hunger() / (double)levelData.get().maxHunger() * 6D);

        ItemStack hpBarItem = hungerBars.get(hpPercentage);
        if(hpBarItem == null) hpBarItem = new ItemStack(Material.AIR);

        hpBarItem = hpBarItem.clone();
        hpBarItem.editMeta(meta -> {
            meta.displayName(Component.text("Hunger: " + pet.health() + "/" + levelData.get().maxHealth()));
        });

        return hpBarItem;
    }

    private void renamePressed(Player player, PetModel pet){
        player.closeInventory();

        PetRenameChatListener.addRenameReq(player, pet);
        player.sendMessage(Component.text("What's the new name for your pet? (write 'cancel' to cancel)"));
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