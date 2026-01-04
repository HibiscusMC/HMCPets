package com.hibiscusmc.hmcpets.config;

import com.hibiscusmc.hmcpets.gui.ListPetsMenu;
import com.hibiscusmc.hmcpets.gui.MyPetMenu;
import com.hibiscusmc.hmcpets.gui.PetLevelsMenu;
import com.hibiscusmc.hmcpets.gui.internal.PetMenu;
import lombok.Getter;
import lombok.extern.java.Log;
import org.bukkit.plugin.Plugin;
import team.unnamed.inject.Inject;
import team.unnamed.inject.Injector;
import team.unnamed.inject.Singleton;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

@Log(topic = "HMCPets")
@Singleton
public class MenuConfig {

    @Inject
    private Plugin plugin;
    @Inject
    private Injector injector;

    @Getter
    private ListPetsMenu listPetsMenu;

    @Getter
    private MyPetMenu myPetMenu;

    @Getter
    private PetLevelsMenu petLevelsMenu;

    public void setup() {
        log.info("Loading menus...");

        Path path = new File(plugin.getDataFolder().getPath(), "menus").toPath();
        File petsFile = path.toFile();

        if (!petsFile.exists()) {
            petsFile.mkdirs();

            plugin.saveResource("menus" + File.separator + "list_pets.yml", false);
            plugin.saveResource("menus" + File.separator + "my_pet.yml", false);
            plugin.saveResource("menus" + File.separator + "pet_levels.yml", false);
        }

        listPetsMenu = new ListPetsMenu(path.resolve("list_pets.yml"));
        myPetMenu = new MyPetMenu(path.resolve("my_pet.yml"));
        petLevelsMenu = new PetLevelsMenu(path.resolve("pet_levels.yml"));

        List<PetMenu> menusToLoad = List.of(listPetsMenu, myPetMenu, petLevelsMenu);

        for (PetMenu menu : menusToLoad) {
            injector.injectMembers(menu);
            menu.setup();
        }

        log.info("All menus loaded.");
    }

}