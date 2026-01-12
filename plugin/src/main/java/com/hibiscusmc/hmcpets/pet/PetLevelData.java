package com.hibiscusmc.hmcpets.pet;

import com.hibiscusmc.hmcpets.api.data.IPetLevelData;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter @AllArgsConstructor @ToString
public class PetLevelData implements IPetLevelData {

    private int maxHealth, maxHunger;
    private int level;
    private int expRequired;

    private String mmTickSkill, mmSpawnSkill, mmDeathSkill;

}
