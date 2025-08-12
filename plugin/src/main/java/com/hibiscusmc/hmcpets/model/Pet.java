package com.hibiscusmc.hmcpets.model;

import com.hibiscusmc.hmcpets.i18n.LangConfig;
import com.hibiscusmc.hmcpets.i18n.LangEntry;
import com.hibiscusmc.hmcpets.pet.PetData;
import lombok.Data;
import org.bukkit.inventory.ItemStack;
import team.unnamed.inject.Inject;

import java.lang.reflect.InvocationTargetException;

@Data
public class Pet {

    private final int id;

    private final User owner;
    private final PetData config;

    private String name;
    private int level;
    private long experience;

    private Skin skin;
    private Rarity rarity;
    private Collar collar;
    private ItemStack craving;

    private long obtainedTimestamp;
    private long lastFed;

    private Status status;
    private int power;
    private double health;
    private double attack;
    private double hunger;

    public enum Status {
        IDLE,
        ACTIVE,
        RESTING;

        public static Status of(String status) {
            try {
                return valueOf(status.toUpperCase());
            } catch (IllegalArgumentException | NullPointerException e) {
                return IDLE;
            }
        }
    }

    public enum Rarity {
        COMMON("petsRarityCommon"),
        RARE("petsRarityRare"),
        EPIC("petsRarityEpic"),
        LEGENDARY("petsRarityLegendary");

        private final String id;

        @Inject
        private LangConfig langConfig;

        Rarity(String id) {
            this.id = id;
        }

        public LangEntry getName() {
            try {
                return (LangEntry) langConfig.getClass()
                        .getMethod(this.id)
                        .invoke(langConfig);
            } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
                return new LangEntry("<unspecified>");
            }
        }

        public static Rarity of(String rarity) {
            try {
                return valueOf(rarity.toUpperCase());
            } catch (IllegalArgumentException e) {
                return Rarity.COMMON;
            }
        }
    }
}
