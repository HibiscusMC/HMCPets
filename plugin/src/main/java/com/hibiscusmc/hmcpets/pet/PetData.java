package com.hibiscusmc.hmcpets.pet;

import com.hibiscusmc.hmcpets.config.AbstractConfig;
import com.hibiscusmc.hmcpets.model.Collar;
import com.hibiscusmc.hmcpets.model.Skin;
import lombok.Getter;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

@Getter
public class PetData extends AbstractConfig {
    private final String category;
    private final String id;

    private final Map<String, Skin> skins;
    private final Map<String, Collar> collars;

    public PetData(Path path, String id, String category) {
        super(path);

        this.id = id;
        this.category = category;

        skins = new HashMap<>();
        collars = new HashMap<>();
    }

    public void setup() {
        load();
    }
}
