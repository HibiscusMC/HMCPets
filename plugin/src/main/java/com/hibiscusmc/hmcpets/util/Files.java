package com.hibiscusmc.hmcpets.util;

import com.hibiscusmc.hmcpets.api.HMCPets;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Files {

    public static List<File> listFiles(File path) {
        if (!path.isDirectory()) {
            return null;
        }

        return List.of(Objects.requireNonNull(path.listFiles(File::isFile)));
    }

    public static List<File> listDirs(File path) {
        List<File> folders = new ArrayList<>();
        folders.add(path);

        if (!path.isDirectory()) {
            return folders;
        }

        folders.addAll(List.of(Objects.requireNonNull(path.listFiles(File::isDirectory))));
        return folders;
    }

    public static File findOrCreate(String folder, String path) {
        Path fetchedPath = Path.of(folder, path);

        if (!fetchedPath.toFile().exists()) {
            HMCPets.instance()
                    .saveResource(path, false);
        }

        return fetchedPath.toFile();
    }

}