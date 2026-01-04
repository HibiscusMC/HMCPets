package com.hibiscusmc.hmcpets.api.data;

public interface IPetLevelData {

    int level();

    //Skills for MythicMobs support TODO
    String mmTickSkill();
    String mmSpawnSkill();
    String mmDeathSkill();

    int expToNextLevel();
}
