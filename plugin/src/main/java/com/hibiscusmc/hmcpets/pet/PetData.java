package com.hibiscusmc.hmcpets.pet;

import com.hibiscusmc.hmcpets.api.model.CollarModel;
import com.hibiscusmc.hmcpets.api.data.IPetData;
import com.hibiscusmc.hmcpets.api.model.SkinModel;
import com.hibiscusmc.hmcpets.api.model.registry.PetType;
import com.hibiscusmc.hmcpets.config.internal.AbstractConfig;
import lombok.Getter;
import me.lojosho.shaded.configurate.serialize.SerializationException;
import org.bukkit.inventory.ItemStack;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

@Getter
public class PetData extends AbstractConfig implements IPetData {

    private final String category;
    private final String id;

    private final Map<String, CollarModel> collars;
    private final Map<String, SkinModel> skins;

    private PetType type;
    private ItemStack icon;

    public PetData(Path path, String id, String category) {
        super(path);

        this.id = id;
        this.category = category;

        collars = new HashMap<>();
        skins = new HashMap<>();
    }

    public void setup() {
        load();

        try {
            icon = get("icon").get(ItemStack.class);
        } catch (SerializationException e) {
            throw new RuntimeException(e);
        }
    }

}