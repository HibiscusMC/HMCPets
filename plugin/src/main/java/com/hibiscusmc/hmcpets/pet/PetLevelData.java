package com.hibiscusmc.hmcpets.pet;

import com.hibiscusmc.hmcpets.api.data.IPetLevelData;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter @AllArgsConstructor @ToString
public class PetLevelData implements IPetLevelData {

    private List<String> lore;

    private int maxHealth, maxHunger;
    private int level;
    private int expRequired;

    private String mmTickSkill, mmSpawnSkill, mmDeathSkill;
}
