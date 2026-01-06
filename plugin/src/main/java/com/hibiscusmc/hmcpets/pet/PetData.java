package com.hibiscusmc.hmcpets.pet;

import com.hibiscusmc.hmcpets.api.HMCPets;
import com.hibiscusmc.hmcpets.api.data.IPetData;
import com.hibiscusmc.hmcpets.api.data.IPetLevelData;
import com.hibiscusmc.hmcpets.api.model.CollarModel;
import com.hibiscusmc.hmcpets.api.model.SkinModel;
import com.hibiscusmc.hmcpets.api.model.registry.MobType;
import com.hibiscusmc.hmcpets.api.model.registry.PetRarity;
import com.hibiscusmc.hmcpets.api.model.registry.PetType;
import com.hibiscusmc.hmcpets.config.internal.AbstractConfig;
import lombok.Getter;
import me.lojosho.hibiscuscommons.hooks.Hooks;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Getter
public class PetData extends AbstractConfig implements IPetData {

    private final String category;
    private final String id;

    private String permission;

    private PetRarity rarity;
    private int petPoints;

    private int maxLevel;
    private Map<Integer, IPetLevelData> levels;

    private final Map<String, CollarModel> collars;
    private final Map<String, SkinModel> skins;

    private PetType type;
    private ItemStack icon, rawIcon;

    private String mobID;
	private MobType mobType;


    private boolean useDefaultFollowAlgorithm;

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

	    type = HMCPets.instance().petTypeRegistry().getRegistered(get("type").getString().toLowerCase()).orElseThrow(NoSuchFieldError::new);

	    Material iconMat = Material.getMaterial(get("icon").getString("STONE"));
	    icon = (iconMat == null) ? Hooks.getItem(get("icon").getString("STONE")) : new ItemStack(iconMat);
		mobID = get("mob-id").getString();

        mobType = HMCPets.instance().mobTypeRegistry().getRegistered(get("mob-type").getString("vanilla").toLowerCase()).orElseThrow(NoSuchFieldError::new);

        petPoints = get("pet-points").getInt();
        rarity = HMCPets.instance().petRarityRegistry().getRegistered(get("rarity").getString("common").toLowerCase()).orElseThrow(NoSuchFieldError::new);

        if(icon == null) {
            System.out.println("Malformed config (icon " + get("icon").getString() + " not found): " + id + ". Aborting loading this pet!");
            return;
        }

        rawIcon = icon.clone();

        permission = get("permission").getString("hmcpets.pet." + id);

        useDefaultFollowAlgorithm = get("use-default-follow-algorithm").getBoolean(true);

	    System.out.println("Loaded " + id + " pet (" + type.id() + ", icon: " + icon.getType().name() + ")");
    }

    @Override
    public Optional<IPetLevelData> getLevel(int level) {
        return Optional.ofNullable(levels().get(level));
    }

    @Override
    public boolean useDefaultFollowAlgorithm() {
        return useDefaultFollowAlgorithm;
    }
}