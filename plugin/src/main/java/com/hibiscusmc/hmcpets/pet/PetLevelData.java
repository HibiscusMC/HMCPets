package com.hibiscusmc.hmcpets.pet;

import com.hibiscusmc.hmcpets.api.data.IPetLevelData;
import lombok.Getter;

@Getter
public class PetLevelData implements IPetLevelData {

    private int level;
    private int expToNextLevel;

    private String mmTickSkill, mmSpawnSkill, mmDeathSkill;

}
