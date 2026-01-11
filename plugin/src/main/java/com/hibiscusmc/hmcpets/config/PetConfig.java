package com.hibiscusmc.hmcpets.config;

import com.hibiscusmc.hmcpets.pet.PetData;
import com.hibiscusmc.hmcpets.util.Files;
import lombok.Getter;
import lombok.extern.java.Log;
import org.bukkit.plugin.Plugin;
import team.unnamed.inject.Inject;
import team.unnamed.inject.Singleton;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Log(topic = "HMCPets")
@Singleton
public class PetConfig {

    @Getter
    private final Map<String, PetData> allPets = new HashMap<>();

    @Inject
    private Plugin plugin;

    public void setup() {
        log.info("Loading pets...");
        allPets.clear();

        Path path = new File(plugin.getDataFolder().getPath(), "pets").toPath();
        File petsFile = path.toFile();

        if (!petsFile.exists()) {
            petsFile.mkdirs();

            new File(path.toString(), "example_category").mkdirs();

            plugin.saveResource("pets" + File.separator + "example_category" + File.separator + "doggo.yml", false);
        }

        List<File> folders = Files.listDirs(petsFile);
        for (File folder : folders) {
            List<File> files = Files.listFiles(folder);
            if (files == null) {
                continue;
            }

            String category;
            if (folder.getParentFile().getName().equalsIgnoreCase("HMCPets")) {
                category = "unspecified";
            } else {
                category = folder.getName();
            }

            for (File file : files) {
                PetData petData = new PetData(file.toPath(), file.getName().split("\\.")[0], category);
                boolean loaded = petData.setup();
                if(!loaded) continue;

                allPets.put(petData.id(), petData);
                log.info(file.getName().split("\\.")[0] + " pet loaded.");
            }
        }

        log.info(allPets.size() + " pets loaded.");
    }

    public Optional<PetData> getPetData(String petID){
        return Optional.ofNullable(allPets.get(petID));
    }

}