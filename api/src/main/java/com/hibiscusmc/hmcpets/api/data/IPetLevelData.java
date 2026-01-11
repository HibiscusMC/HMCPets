package com.hibiscusmc.hmcpets.api.data;

public interface IPetLevelData {

    int maxHealth();
    int level();

    //Skills for MythicMobs support TODO
    String mmTickSkill();
    String mmSpawnSkill();
    String mmDeathSkill();

    int expRequired();
}
