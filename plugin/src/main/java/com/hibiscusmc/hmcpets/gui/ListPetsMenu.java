package com.hibiscusmc.hmcpets.gui;

import com.hibiscusmc.hmcpets.api.gui.Button;
import com.hibiscusmc.hmcpets.api.model.enums.PetStatus;
import com.hibiscusmc.hmcpets.config.PluginConfig;
import com.hibiscusmc.hmcpets.config.internal.AbstractConfig;
import com.hibiscusmc.hmcpets.api.model.PetModel;
import com.hibiscusmc.hmcpets.api.model.UserModel;
import com.hibiscusmc.hmcpets.config.LangConfig;
import com.hibiscusmc.hmcpets.api.util.Adventure;
import com.hibiscusmc.hmcpets.api.util.Pets;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import me.lojosho.shaded.configurate.ConfigurationNode;
import me.lojosho.shaded.configurate.serialize.SerializationException;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.apache.commons.lang3.IntegerRange;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;
import team.unnamed.inject.Inject;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

public class ListPetsMenu extends AbstractConfig {

    private String title;
    private int rows;

    private List<Integer> validSlots;

    private Button nextPageButton;
    private Button previousPageButton;
    private Button filterButton;
    private Button petDataButton;

    private GuiActionUsage actionUsage;

    private List<Button> extraButtons;

    @Inject
    private LangConfig langConfig;
    @Inject
    private PluginConfig pluginConfig;
    @Inject
    private Plugin instance;

    public ListPetsMenu(Path path) {
        super(path);
    }

    public void setup() {
        load();

        title = null;
        rows = 0;
        validSlots = new ArrayList<>();
        nextPageButton = null;
        previousPageButton = null;
        filterButton = null;
        petDataButton = null;
        extraButtons = new ArrayList<>();

        title = get("title").getString("All Pets");
        rows = get("rows").getInt(5);

        try {
            for (String slot : Objects.requireNonNull(get("slots").getList(String.class))) {
                if (slot.contains("-")) {
                    String[] split = slot.split("-");
                    IntegerRange range = IntegerRange.of(Integer.parseInt(split[0]), Integer.parseInt(split[1]));

                    for (int i = range.getMinimum(); i <= range.getMaximum(); i++) {
                        validSlots.add(i);
                    }
                } else {
                    validSlots.add(Integer.parseInt(slot));
                }
            }

            for (var entry : get("icons").childrenMap().entrySet()) {
                String iconKey = entry.getKey().toString();

                switch (iconKey) {
                    case "next-page": {
                        ItemStack item = parse(get("icons.next-page.item").get(ItemStack.class));
                        boolean dynamic = get("icons.next-page.dynamic").getBoolean();
                        int slot = get("icons.next-page.slot").getInt();

                        nextPageButton = Button.of(item, slot, dynamic);
                        break;
                    }
                    case "previous-page": {
                        ItemStack item = parse(get("icons.previous-page.item").get(ItemStack.class));
                        boolean dynamic = get("icons.previous-page.dynamic").getBoolean();
                        int slot = get("icons.previous-page.slot").getInt();

                        previousPageButton = Button.of(item, slot, dynamic);
                        break;
                    }
                    case "filter": {
                        ItemStack item = parse(get("icons.filter.item").get(ItemStack.class));
                        int slot = get("icons.filter.slot").getInt();

                        filterButton = Button.of(item, slot);
                        break;
                    }
                    case "pet-data": {
                        ConfigurationNode usageNode = get("icons.pet-data.usage");

                        configNode.node("icons", "pet-data", "item", "material").set("STONE");
                        ItemStack item = parse(get("icons.pet-data.item").get(ItemStack.class));

                        petDataButton = Button.of(item);

                        actionUsage = new GuiActionUsage(
                                new GuiActionUsageFavorites(
                                        usageNode.node("favorites", "add").getString(),
                                        usageNode.node("favorites", "remove").getString()
                                ),
                                new GuiActionUsageSummon(
                                        usageNode.node("summon", "summon").getString(),
                                        usageNode.node("summon", "unsummon").getString(),
                                        usageNode.node("summon", "resting").getString(),
                                        usageNode.node("summon", "max-active").getString()
                                )
                        );
                        break;
                    }
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
            throw new RuntimeException("Cannot parse list_pets.yml menu: " + e.getMessage());
        }
    }

    public void open(Player player, UserModel user, Set<PetModel> pets) {
        PaginatedGui gui = Gui.paginated()
                .title(Adventure.parse(title))
                .rows(rows)
                .pageSize(validSlots.size())
                .disableAllInteractions()
                .create();

        AtomicReference<Filter> filter = new AtomicReference<>(Filter.ALL);

        for (int i = 0; i < rows * 9; i++) {
            if (validSlots.contains(i)) {
                continue;
            }

            gui.setItem(i, new GuiItem(Material.AIR));
        }

        if (filterButton != null) {
            gui.setItem(filterButton.slot(),
                    new GuiItem(parseFilter(filterButton.item().clone(), filter),
                            (event) -> handleFilterClick(event.getClick(), gui, filterButton, filter, user, pets)
                    ));
        }

        if (nextPageButton != null) {
            if (nextPageButton.dynamic() && gui.getCurrentPageNum() + 1 < gui.getPagesNum()) {
                gui.setItem(nextPageButton.slot(), new GuiItem(nextPageButton.item(), event -> {
                    gui.next();
                }));
            }
        }

        if (previousPageButton != null) {
            if (previousPageButton.dynamic() && gui.getCurrentPageNum() - 1 > 0) {
                gui.setItem(previousPageButton.slot(), new GuiItem(previousPageButton.item(), event -> {
                    gui.previous();
                }));
            }
        }

        for (Button button : extraButtons) {
            gui.setItem(button.slot(), new GuiItem(button.item(), event -> button.runActions((Player) event.getWhoClicked())));
        }

        loadPets(gui, user, pets, filter.get());

        gui.open(player);
    }

    private void handleFilterClick(ClickType click, PaginatedGui gui, Button filterButton, AtomicReference<Filter> filter, UserModel user, Set<PetModel> pets) {
        filter.set(click.isRightClick() ? filter.get().previous() : filter.get().next());

        loadPets(gui, user, pets, filter.get());

        gui.setItem(filterButton.slot(),
                new GuiItem(parseFilter(filterButton.item().clone(), filter),
                        (event) -> handleFilterClick(event.getClick(), gui, filterButton, filter, user, pets)
                ));

        gui.update();
    }

    private void loadPets(PaginatedGui gui, UserModel user, Set<PetModel> pets, Filter filter) {
        gui.clearPageItems();

        for (PetModel pet : pets) {
            boolean isFavorite = user.hasFavoritePet(pet);
            boolean isActive = pet.status() == PetStatus.ACTIVE;
            boolean isResting = pet.status() == PetStatus.RESTING;

            switch (filter) {
                case ACTIVE: {
                    if (!isActive) continue;

                    break;
                }
                case FAVORITE: {
                    if (!isFavorite) continue;

                    break;
                }
                case IDLE: {
                    if (pet.status() != PetStatus.IDLE) continue;

                    break;
                }
                case RESTING: {
                    if (!isResting) continue;

                    break;
                }
            }

            TagResolver usageResolver = TagResolver.resolver("usage", (args, context) -> {
                String arg = args.popOr("none").value();
                String value = switch (arg.toLowerCase()) {
                    case "favorites" -> isFavorite
                            ? actionUsage.favorites().remove()
                            : actionUsage.favorites().add();
                    case "summon" -> {
                        if (isActive) {
                            yield actionUsage.summon().unsummon();
                        } else {
                            yield user.countActivePets() >= pluginConfig.pets().maxActive()
                                    ? actionUsage.summon().maxActive()
                                    : isResting
                                    ? actionUsage.summon().resting()
                                    : actionUsage.summon().summon();
                        }
                    }
                    default -> "unexpected value: " + arg;
                };

                return Tag.inserting(Adventure.parse(value));
            });

            gui.addItem(new GuiItem(Pets.buildIcon(pet, petDataButton, usageResolver), event -> {
                handlePetClick(event, user, pet);

                if (!event.getClick().isRightClick()) {
                    loadPets(gui, user, pets, filter);
                    gui.update();
                }
            }));
        }
    }

    private void handlePetClick(InventoryClickEvent event, UserModel user, PetModel pet) {
        ClickType click = event.getClick();
        Player player = (Player) event.getWhoClicked();

        if (click.isShiftClick()) {
            if (user.hasFavoritePet(pet)) {
                user.removeFavoritePet(pet);
            } else {
                user.addFavoritePet(pet);
            }
        } else if (click.isLeftClick()) {
            if (pet.status() == PetStatus.ACTIVE) {
                user.removeActivePet(pet);
            } else {
                if (user.countActivePets() >= pluginConfig.pets().maxActive()) {
                    langConfig.petsMaxActive().send(event.getWhoClicked());
                } else {
                    user.addActivePet(pet, player.getLocation());
                }
            }
        } else if (click.isRightClick()) {
            // TODO: Open menu to manage pet
        }
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

    private ItemStack parseFilter(ItemStack stack, AtomicReference<Filter> filter) {
        stack.editMeta(meta -> {
            List<Component> oldLore = meta.lore();
            if (oldLore == null) {
                return;
            }

            List<Component> newLore = new ArrayList<>();

            TagResolver resolver = TagResolver.resolver("current", (args, context) -> {
                String filterArg = args.popOr("none").value();

                return Tag.inserting(
                        filterArg.equalsIgnoreCase(filter.get().name()) ?
                                langConfig.constantsCurrentActive().component() :
                                langConfig.constantsCurrentInactive().component()
                );
            });

            for (Component lore : oldLore) {
                String line = Adventure.unparse(lore)
                        .replace("\\<current", "<current");

                newLore.add(Adventure.parseForMeta(line, resolver));
            }

            meta.lore(newLore);
        });

        return stack;
    }

    enum Filter {

        ALL,
        FAVORITE,
        ACTIVE,
        IDLE,
        RESTING;

        public Filter next() {
            Filter[] all = values();
            int idx = this.ordinal();

            return all[(idx + 1) % all.length];
        }

        public Filter previous() {
            Filter[] all = values();
            int idx = this.ordinal();

            return all[(idx - 1 + all.length) % all.length];
        }

    }


    record GuiActionUsage(GuiActionUsageFavorites favorites, GuiActionUsageSummon summon) {
    }

    record GuiActionUsageFavorites(String add, String remove) {
    }

    record GuiActionUsageSummon(String summon, String unsummon, String resting, String maxActive) {
    }

}