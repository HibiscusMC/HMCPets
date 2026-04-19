package com.hibiscusmc.hmcpets.api.data;

import com.hibiscusmc.hmcpets.api.i18n.LangEntry;

public interface ILangData {

    LangEntry prefix();
    LangEntry noPermission();
    LangEntry playersOnlyCommand();


    LangEntry commandAdminReload();
    LangEntry commandAdminDebug();


    LangEntry guisPetNotSpawned();
    LangEntry guisHpBarName();
    LangEntry guisHungerBarName();
    LangEntry guisRenamePet();
    LangEntry guisRenameCanceled();
    LangEntry guisRenameDone();
    LangEntry guisLevelStatusLocked();
    LangEntry guisLevelStatusUnlocked();
    LangEntry guisLevelStatusCurrent();
    LangEntry guisEquipped();


    LangEntry petsMaxActive();
    LangEntry petsRarityCommon();
    LangEntry petsRarityRare();
    LangEntry petsRarityEpic();
    LangEntry petsRarityLegendary();
    LangEntry petsTypeAquatic();
    LangEntry petsTypeBeast();
    LangEntry petsTypeMagic();
    LangEntry petsTypeCritter();
    LangEntry petsLeveledUpTitle();
    LangEntry petsLeveledUpSubtitle();
    LangEntry petsDowned();


    LangEntry constantsNoPets();
    LangEntry constantsEnabled();
    LangEntry constantsDisabled();
    LangEntry constantsCurrentActive();
    LangEntry constantsCurrentInactive();

}
