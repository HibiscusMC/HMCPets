package com.hibiscusmc.hmcpets.pet;

import com.hibiscusmc.hmcpets.api.data.IPetLevelData;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public class PetLevelData implements IPetLevelData {

    private int maxHealth;
    private int level;
    private int expRequired;

    private String mmTickSkill, mmSpawnSkill, mmDeathSkill;

}
