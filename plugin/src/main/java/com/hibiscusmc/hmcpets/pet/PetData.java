package com.hibiscusmc.hmcpets.pet;

import com.hibiscusmc.hmcpets.api.HMCPets;
import com.hibiscusmc.hmcpets.api.data.IPetData;
import com.hibiscusmc.hmcpets.api.model.CollarModel;
import com.hibiscusmc.hmcpets.api.model.SkinModel;
import com.hibiscusmc.hmcpets.api.model.registry.PetType;
import com.hibiscusmc.hmcpets.config.internal.AbstractConfig;
import lombok.Getter;
import me.lojosho.hibiscuscommons.hooks.Hooks;
import org.bukkit.Material;
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

	private String mobType;

    public PetData(Path path, String id, String category) {
        super(path);

        this.id = id;
        this.category = category;

        collars = new HashMap<>();
        skins = new HashMap<>();
    }

    public void setup() {
        load();

		if(get("type").getString() == null) {
			System.out.println("Malformed config: " + id + ". Aborting loading this pet!");
			return;
		}

	    type = HMCPets.instance().petTypeRegistry().getRegistered(get("type").getString()).orElseThrow(NoSuchFieldError::new);

	    Material iconMat = Material.getMaterial(get("icon").getString());
	    icon = (iconMat == null) ? Hooks.getItem(get("icon").getString()) : new ItemStack(iconMat);
		mobType = get("mob-type").getString();

        if(icon == null) {
            System.out.println("Malformed config (icon " + get("icon").getString() + " not found): " + id + ". Aborting loading this pet!");
            return;
        }

	    System.out.println("Loaded " + id + " pet (" + type.id() + ", icon: " + icon.getType().name() + ")");
    }

}