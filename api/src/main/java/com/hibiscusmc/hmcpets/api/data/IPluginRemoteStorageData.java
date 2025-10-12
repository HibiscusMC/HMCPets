package com.hibiscusmc.hmcpets.api.data;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface IPluginRemoteStorageData {

    @Nullable
    String uri();

    @NotNull
    String address();

    int port();

    @NotNull
    String username();

    @NotNull
    String password();

}
