package com.hibiscusmc.hmcpets.api.data;

import com.hibiscusmc.hmcpets.api.model.CollarModel;
import com.hibiscusmc.hmcpets.api.model.SkinModel;
import com.hibiscusmc.hmcpets.api.model.registry.PetRarity;
import com.hibiscusmc.hmcpets.api.model.registry.PetType;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public interface IPetData {

    String category();
    String id();
    PetType type();
    PetRarity rarity();
    int petPoints();
    ItemStack icon();
    ItemStack rawIcon();
    String mobType();

    Map<String, CollarModel> collars();
    Map<String, SkinModel> skins();

}
