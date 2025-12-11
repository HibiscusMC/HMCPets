package com.hibiscusmc.hmcpets.api.util;

import com.hibiscusmc.hmcpets.api.gui.Button;
import com.hibiscusmc.hmcpets.api.model.PetModel;
import io.papermc.paper.datacomponent.DataComponentTypes;
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
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public class Pets {

    private static final ZoneId ZONE_ID
            = ZoneId.systemDefault();
    private static final DateTimeFormatter DATE_FORMATTER
            = DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy HH:mm:ss");

    //TODO: This doesn't allow for i18n. Either LangConfig needs to be moved here or this needs to be
    //TODO: moved to plugin module
    public static ItemStack buildIcon(PetModel pet, Button petButton, TagResolver... resolvers) {
        ItemStack stack = pet.config().icon() == null ? ItemStack.of(Material.PLAYER_HEAD) : pet.config().icon();
        stack.copyDataFrom(petButton.item(), Set.of(
                DataComponentTypes.CUSTOM_NAME,
                DataComponentTypes.LORE
        )::contains);

        stack.editMeta(meta -> {
            TagResolver resolver = TagResolver.resolver("pet", (args, context) -> {
                String switchArg = args.popOr("none").value();

                String name = switch (switchArg.toLowerCase()) {
                    case "name" -> pet.name();
                    case "type" -> pet.config().type() == null ? "unknown" : pet.config().type().name().string();
                    case "id" -> pet.id() + "";
                    case "level" -> pet.level() + "";
                    case "experience" -> String.format("%,d", pet.experience());
                    case "rarity" -> pet.rarity() == null ? "unknown" : pet.rarity().name().string();
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

            TagResolver usageResolver = TagResolver.resolver("usage", (args, context) -> {
                String switchArg = args.popOr("none").value();

                String name = switch (switchArg.toLowerCase()) {
                    case "summon" -> {
                        if(pet.entity() == null) yield "<#d24c9f>Left-Click<dark_gray>: <white>Summon your pet";

                        yield "<#d24c9f>Left-Click<dark_gray>: <white>Store your pet";
                    }
                    default -> "unexpected value: " + switchArg;
                };

                return Tag.inserting(Adventure.parse(name));
            });

            List<TagResolver> resolverList = Stream.concat(
                    Stream.of(resolver, usageResolver),
                    Arrays.stream(resolvers)
            ).toList();

            if (meta.customName() != null) {
                Component name = Adventure.parseForMeta(Adventure.unparse(meta.customName())
                                .replace("\\<pet", "<pet")
                                .replace("\\<usage", "<usage"),
                        TagResolver.resolver(resolverList));

                meta.customName(name);
            }

            List<Component> oldLore = meta.lore();
            List<Component> newLore = new ArrayList<>();

            if (oldLore != null) {
                for (Component lore : oldLore) {
                    String line = Adventure.unparse(lore)
                            .replace("\\<pet", "<pet")
                            .replace("\\<usage", "<usage");

                    newLore.add(Adventure.parseForMeta(line, TagResolver.resolver(resolverList)));
                }

                meta.lore(newLore);
            }
        });

        return stack;
    }

}