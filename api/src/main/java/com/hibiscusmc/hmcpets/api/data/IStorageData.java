package com.hibiscusmc.hmcpets.api.data;

import com.hibiscusmc.hmcpets.api.storage.StorageMethod;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface IStorageData {

    @NotNull
    StorageMethod method();

    @Nullable
    IRemoteStorageData remote();

    @NotNull
    String database();

    @NotNull
    String prefix();

}