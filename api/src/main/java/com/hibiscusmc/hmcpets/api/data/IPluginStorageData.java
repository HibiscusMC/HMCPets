package com.hibiscusmc.hmcpets.api.data;

import com.hibiscusmc.hmcpets.api.storage.StorageMethod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface IPluginStorageData {

    @NotNull
    StorageMethod method();

    @Nullable
    IPluginRemoteStorageData remote();

    @NotNull
    String database();

    @NotNull
    String prefix();

}