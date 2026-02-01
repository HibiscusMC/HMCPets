package com.hibiscusmc.hmcpets.api.data;

import java.util.List;

public interface IPetLevelData {

    List<String> lore();

    int maxHealth();
    int maxHunger();
    int level();

    //Skills for MythicMobs support TODO
    String mmTickSkill();
    String mmSpawnSkill();
    String mmDeathSkill();

    int expRequired();
}
