package com.hibiscusmc.hmcpets.config;

import com.hibiscusmc.hmcpets.gui.ListPetsMenu;
import lombok.extern.java.Log;
import org.bukkit.plugin.Plugin;
import team.unnamed.inject.Inject;
import team.unnamed.inject.Injector;
import team.unnamed.inject.Singleton;

import java.io.File;
import java.nio.file.Path;

@Log(topic = "HMCPets")
@Singleton
public class MenuConfig {

    @Inject
    private Plugin plugin;
    @Inject
    private Injector injector;

    private ListPetsMenu listPetsMenu;

    public void setup() {
        log.info("Loading menus...");

        Path path = new File(plugin.getDataFolder().getPath(), "menus").toPath();
        File petsFile = path.toFile();

        if (!petsFile.exists()) {
            petsFile.mkdirs();

            plugin.saveResource("menus" + File.separator + "list_pets.yml", false);
        }

        listPetsMenu = new ListPetsMenu(path.resolve("list_pets.yml"));
        injector.injectMembers(listPetsMenu);
        listPetsMenu.setup();

        log.info("All menus loaded.");
    }

    public ListPetsMenu listPetsMenu() {
        return listPetsMenu;
    }

}