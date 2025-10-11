package com.hibiscusmc.hmcpets.api.util;

import com.hibiscusmc.hmcpets.api.data.ILangData;
import com.hibiscusmc.hmcpets.api.gui.Button;
import com.hibiscusmc.hmcpets.api.model.PetModel;
import com.hibiscusmc.hmcpets.api.model.enums.PetRarity;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class Pets {

    private static final ZoneId ZONE_ID
            = ZoneId.systemDefault();
    private static final DateTimeFormatter DATE_FORMATTER
            = DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy HH:mm:ss");

    public static ItemStack buildIcon(ILangData langData, PetModel pet, Button petButton) {
        ItemStack stack = petButton.item().withType(Material.PLAYER_HEAD);

        stack.editMeta(meta -> {
            List<Component> oldLore = meta.lore();
            List<Component> newLore = new ArrayList<>();

            TagResolver resolver = TagResolver.resolver("pet", (args, context) -> {
                String switchArg = args.popOr("none").value();

                String name = switch (switchArg.toLowerCase()) {
                    case "name" -> pet.name();
                    case "id" -> pet.id() + "";
                    case "level" -> pet.level() + "";
                    case "experience" -> String.format("%,d", pet.experience());
                    case "rarity" -> parsePetRarity(langData, pet.rarity());
                    case "collar" -> pet.collar() == null ? "None" : pet.collar().name();
                    case "craving" -> {
                        ItemStack craving = pet.craving();
                        if (craving == null) yield "Nothing";

                        yield "x" + craving.getAmount() + " " + Adventure.unparse(Items.getItemName(craving));
                    }
                    case "power" -> pet.power() + "";
                    case "health" -> pet.health() + "";
                    case "attack" -> pet.attack() + "";
                    case "hunger" -> pet.hunger() + "";
                    case "obtained" -> {
                        String lastArg = args.popOr("none").value();
                        LocalDate obtainedDate = Instant.ofEpochMilli(pet.obtainedTimestamp()).atZone(ZONE_ID).toLocalDate();
                        LocalDate now = LocalDate.now();

                        if (lastArg.equalsIgnoreCase("short")) {
                            long daysAgo = ChronoUnit.DAYS.between(obtainedDate, now);
                            yield daysAgo + " days ago";
                        } else {
                            yield obtainedDate.atStartOfDay().format(DATE_FORMATTER);
                        }
                    }
                    default -> "unexpected value: " + switchArg;
                };

                return Tag.inserting(Adventure.parse(name));
            });

            Component name = Adventure.parseForMeta(Adventure.unparse(meta.customName())
                    .replace("\\<pet", "<pet"), resolver);
            meta.customName(name);

            if (oldLore == null) {
                return;
            }

            for (Component lore : oldLore) {
                String line = Adventure.unparse(lore)
                        .replace("\\<pet", "<pet");

                newLore.add(Adventure.parseForMeta(line, resolver));
            }

            meta.lore(newLore);
        });

        return stack;
    }

    public static String parsePetRarity(ILangData langData, PetRarity rarity) {
        return switch (rarity) {
            case RARE -> langData.petsRarityRare().string();
            case EPIC -> langData.petsRarityEpic().string();
            case LEGENDARY -> langData.petsRarityLegendary().string();
            default -> langData.petsRarityCommon().string();
        };
    }

}