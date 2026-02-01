package com.hibiscusmc.hmcpets.api.model;

import com.hibiscusmc.hmcpets.api.model.registry.MobType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

@Getter
@AllArgsConstructor
public class SkinModel {

    private String id;
    private String mobId;
    private MobType mobType;
    private ItemStack icon;

}