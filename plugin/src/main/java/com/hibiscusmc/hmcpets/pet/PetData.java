package com.hibiscusmc.hmcpets.pet;

import com.hibiscusmc.hmcpets.api.model.CollarModel;
import com.hibiscusmc.hmcpets.api.data.IPetData;
import com.hibiscusmc.hmcpets.api.model.SkinModel;
import com.hibiscusmc.hmcpets.api.model.enums.PetType;
import com.hibiscusmc.hmcpets.config.internal.AbstractConfig;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class PetData extends AbstractConfig implements IPetData {

    private final String category;
    private final String id;

    private final Map<String, CollarModel> collars;
    private final Map<String, SkinModel> skins;

    private PetType type;

    public PetData(Path path, String id, String category) {
        super(path);

        this.id = id;
        this.category = category;

        collars = new HashMap<>();
        skins = new HashMap<>();
    }

    public void setup() {
        load();
    }

    @Override
    public String category() {
        return this.category;
    }

    @Override
    public String id() {
        return this.id;
    }

    @Override
    public PetType type() {
        return this.type;
    }

    @Override
    public Map<String, CollarModel> collars() {
        return this.collars;
    }

    @Override
    public Map<String, SkinModel> skins() {
        return this.skins;
    }

}