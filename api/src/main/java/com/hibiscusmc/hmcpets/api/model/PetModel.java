package com.hibiscusmc.hmcpets.api.model;

import com.hibiscusmc.hmcpets.api.data.IPetData;
import com.hibiscusmc.hmcpets.api.model.registry.PetRarity;
import com.hibiscusmc.hmcpets.api.model.enums.PetStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

@Data
@EqualsAndHashCode(exclude = "owner")
public class PetModel {

    private final int id;

    @ToString.Exclude
    private final UserModel owner;
    private final IPetData config;

    private String name;
    private int level;
    private long experience;

    private LivingEntity entity;

    private SkinModel skin;
    private PetRarity rarity;
    private CollarModel collar;
    private ItemStack craving;

    private long obtainedTimestamp;
    private long lastFed;

    private PetStatus status;
    private int power;
    private double health;
    private double attack;
    private double hunger;

}