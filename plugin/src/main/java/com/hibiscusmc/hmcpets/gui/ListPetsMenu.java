package com.hibiscusmc.hmcpets.gui;

import com.hibiscusmc.hmcpets.config.AbstractConfig;
import me.lojosho.shaded.configurate.serialize.SerializationException;
import org.apache.commons.lang.math.IntRange;
import org.bukkit.inventory.ItemStack;

import java.nio.file.Path;
import java.util.List;

public class ListPetsMenu extends AbstractConfig {

    private String title;
    private int rows;

    private List<Integer> validSlots;

    private Button nextPageButton;
    private Button previousPageButton;
    private Button filterButton;

    private List<Button> extraButtons;

    public ListPetsMenu(Path path) {
        super(path);
    }

    public void setup() {
        load();

        title = get("title").getString("All Pets");
        rows = get("rows").getInt(5);

        try {
            for (String slot : get("slots").getList(String.class)) {
                if (slot.contains("-")) {
                    String[] split = slot.split("-");
                    IntRange range = new IntRange(Integer.parseInt(split[0]), Integer.parseInt(split[1]));

                    for (int i : range.toArray()) {
                        validSlots.add(i);
                    }
                } else {
                    validSlots.add(Integer.parseInt(slot));
                }
            }

            for (var entry : get("icons").childrenMap().entrySet()) {
                String iconKey = entry.getKey().toString();

                switch (iconKey) {
                    case "next-page":
                        ItemStack item = get("icons." + iconKey + ".item").get(ItemStack.class);
                        break;
                    case "previous-page":

                        break;
                    case "filter":

                        break;
                    default:

                        break;
                }
            }
        } catch (SerializationException | NullPointerException e) {
            throw new RuntimeException("Cannot parse valid slots for list_pets.yml menu.");
        }
    }

    class Button {
        private ItemStack item;
        private int slot;
        private boolean dynamic;

        public Button of(ItemStack item, Integer slot) {
            return of(item, slot, false);
        }

        public Button of(ItemStack item, Integer slot, boolean dynamic) {
            this.item = item;
            this.slot = slot;
            this.dynamic = dynamic;
            return this;
        }
    }
}