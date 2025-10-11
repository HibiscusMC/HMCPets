package com.hibiscusmc.hmcpets.api.data;

import com.hibiscusmc.hmcpets.api.i18n.LangEntry;

public interface ILangData {

    LangEntry prefix();

    LangEntry noPermission();

    LangEntry commandAdminReload();

    LangEntry commandAdminDebug();

    LangEntry commandMainHelp();

    LangEntry commandUsage();

    LangEntry petsRarityCommon();

    LangEntry petsRarityRare();

    LangEntry petsRarityEpic();

    LangEntry petsRarityLegendary();

    LangEntry constantsNoPets();

    LangEntry constantsEnabled();

    LangEntry constantsDisabled();

    LangEntry constantsCurrentActive();

    LangEntry constantsCurrentInactive();

}
