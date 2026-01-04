package com.hibiscusmc.hmcpets.api.data;

import com.hibiscusmc.hmcpets.api.model.CollarModel;
import com.hibiscusmc.hmcpets.api.model.SkinModel;
import com.hibiscusmc.hmcpets.api.model.registry.MobType;
import com.hibiscusmc.hmcpets.api.model.registry.PetRarity;
import com.hibiscusmc.hmcpets.api.model.registry.PetType;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.Optional;

public interface IPetData {

    String category();
    String id();

    String mobID();
    MobType mobType();

    PetType type();
    PetRarity rarity();

    int petPoints();

    int maxLevel();
    Map<Integer, IPetLevelData> levels();


    ItemStack icon();
    ItemStack rawIcon();

    Map<String, CollarModel> collars();
    Map<String, SkinModel> skins();

    Optional<IPetLevelData> getLevel(int level);


    boolean useDefaultFollowAlgorithm();
}
