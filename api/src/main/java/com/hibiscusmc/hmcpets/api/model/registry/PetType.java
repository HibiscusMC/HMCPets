package com.hibiscusmc.hmcpets.api.model.registry;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.hibiscusmc.hmcpets.api.i18n.LangEntry;
import com.hibiscusmc.hmcpets.api.model.PetModel;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import net.kyori.adventure.key.Key;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Data
@Setter(AccessLevel.NONE)
public class PetType {

    private final Key key;

    private final Multimap<ActionType, Consumer<PetModel>> skills = HashMultimap.create();
    private final List<ItemStack> cravings = new ArrayList<>();

    @Setter(AccessLevel.PUBLIC)
    private LangEntry name;

    public String id() {
        return key.value();
    }

    public void registerSkill(ActionType action, Consumer<PetModel> skill) {
        this.skills.put(action, skill);
    }

    public void runSkills(ActionType action, PetModel pet) {
        for (Consumer<PetModel> skill : skills.get(action)) {
            skill.accept(pet);
        }
    }

    public void registerCraving(ItemStack item) {
        this.cravings.add(item.asQuantity(1));
    }

    public boolean validateCraving(ItemStack item) {
        boolean valid = false;

        for (ItemStack craving : cravings) {
            if (item.isSimilar(craving)) {
                valid = true;
            }
        }

        return valid;
    }

}