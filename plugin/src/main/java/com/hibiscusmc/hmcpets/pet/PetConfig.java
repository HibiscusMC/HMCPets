package com.hibiscusmc.hmcpets.pet;

import com.hibiscusmc.hmcpets.util.Files;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.bukkit.plugin.Plugin;
import team.unnamed.inject.Inject;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log(topic = "HMCPets")
@RequiredArgsConstructor
public class PetConfig {

    @Getter
    private final Map<String, PetData> allPets
            = new HashMap<>();

    private final Plugin plugin;
    private final Path path;

    public void setup() {
        log.info("Loading pets...");
        allPets.clear();

        File petsFile = path.toFile();
        if (!petsFile.exists()) {
            petsFile.mkdirs();

            new File(path.toString(), "example_category").mkdirs();
            new File(path.toString(), "example_category2").mkdirs();

            plugin.saveResource("pets" + File.separator + "example_category" + File.separator + "doggo.yml", false);
            plugin.saveResource("pets" + File.separator + "example_category2" + File.separator + "kitty.yml", false);
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
                petData.setup();

                allPets.put(petData.id(), petData);
                log.info(file.getName().split("\\.")[0] + " pet loaded.");
            }
        }

        log.info(allPets.size() + " pets loaded.");
    }
}