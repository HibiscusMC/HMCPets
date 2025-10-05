package com.hibiscusmc.hmcpets.util;

public enum Permissions {

    USE_PET("pets.%.use");

    private final String permissionName;

    Permissions(String permission) {
        permissionName = "hmcpets." + permission;
    }

    public String permission() {
        return permissionName;
    }

    public String permission(String wildcard) {
        return permissionName.replace("%", wildcard);
    }

}