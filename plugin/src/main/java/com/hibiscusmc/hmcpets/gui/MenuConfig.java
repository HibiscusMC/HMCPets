package com.hibiscusmc.hmcpets.gui;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.bukkit.plugin.Plugin;
import team.unnamed.inject.Inject;

import java.io.File;
import java.nio.file.Path;

@Log(topic = "HMCPets")
@RequiredArgsConstructor
public class MenuConfig {
    private final Path path;

    @Inject
    private Plugin plugin;

    public void setup() {
        log.info("Loading menus...");

        File petsFile = path.toFile();
        if (!petsFile.exists()) {
            petsFile.mkdirs();

            plugin.saveResource("menus" + File.separator + "list_pets.yml", false);
        }

        ListPetsMenu listPetsMenu = new ListPetsMenu(path.resolve("list_pets.yml"));
        listPetsMenu.setup();

        log.info("All menus loaded.");
    }
}
