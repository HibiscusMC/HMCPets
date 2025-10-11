package com.hibiscusmc.hmcpets.api.util;

import io.papermc.paper.adventure.PaperAdventure;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.MutableComponent;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Items {

    public static Component getItemName(ItemStack stack) {
            ItemMeta meta = stack.getItemMeta();

            Component itemDisplay = Component.translatable(stack);
            if (meta != null) {
                if (meta.hasItemName()) {
                    itemDisplay = meta.itemName();
                } else if (meta.hasCustomName()) {
                    itemDisplay = meta.customName()
                            .decorate(TextDecoration.ITALIC);
                }
            }

            net.minecraft.world.item.ItemStack craftStack = CraftItemStack.asNMSCopy(stack);
            MutableComponent component = PaperAdventure
                    .asVanilla(itemDisplay)
                    .copy();

            if (craftStack.has(DataComponents.CUSTOM_NAME)) {
                component = component.withStyle(ChatFormatting.ITALIC);
            }

            return PaperAdventure.asAdventure(component.withStyle(craftStack.getRarity().color()));
    }

}
