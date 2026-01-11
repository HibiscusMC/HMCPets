package com.hibiscusmc.hmcpets.api.gui;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public record Button(ItemStack item, int slot, boolean dynamic, List<String> actions) {

    public static Button of(ItemStack item) {
        return of(item, -1, false, List.of());
    }

    public static Button of(ItemStack item, Integer slot) {
        return of(item, slot, false, List.of());
    }

    public static Button of(ItemStack item, Integer slot, boolean dynamic) {
        return of(item, slot, dynamic, List.of());
    }

    public static Button of(ItemStack item, Integer slot, List<String> actions) {
        return of(item, slot, false, actions);
    }

    public static Button of(ItemStack item, Integer slot, boolean dynamic, List<String> actions) {
        return new Button(item, slot, dynamic, actions);
    }

    public void runActions(Player player) {
        for (String action : actions) {
            String[] split = action.split(":");

            String actionData = PlaceholderAPI.setPlaceholders(player, split[1].trim());
            switch (split[0].toLowerCase()) {
                case "player" -> {
                    player.performCommand(actionData);
                }

                case "console" -> {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), actionData);
                }
            }
        }
    }

}