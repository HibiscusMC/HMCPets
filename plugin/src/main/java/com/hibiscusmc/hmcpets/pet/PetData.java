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
import me.lojosho.shaded.configurate.CommentedConfigurationNode;
import me.lojosho.shaded.configurate.serialize.SerializationException;
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

        levels = new HashMap<>();
        collars = new HashMap<>();
        skins = new HashMap<>();
    }

    public boolean setup() {
        load();

		if(get("type").getString() == null) {
			System.out.println("Malformed config: " + id + ". Aborting loading this pet!");
			return false;
		}

	    type = HMCPets.instance().petTypeRegistry().getRegistered(get("type").getString().toLowerCase()).orElseThrow(NoSuchFieldError::new);

	    Material iconMat = Material.getMaterial(get("icon").getString("STONE"));
	    icon = (iconMat == null) ? Hooks.getItem(get("icon").getString("STONE")) : new ItemStack(iconMat);
		mobID = get("mob-id").getString();

        mobType = HMCPets.instance().mobTypeRegistry().getRegistered(get("mob-type").getString("vanilla").toLowerCase()).orElseThrow(NoSuchFieldError::new);

        petPoints = get("pet-points").getInt();
        rarity = HMCPets.instance().petRarityRegistry().getRegistered(get("rarity").getString("common").toLowerCase()).orElseThrow(NoSuchFieldError::new);

        if(icon == null) {
            System.out.println("Malformed config (icon " + get("icon").getString() + " not found): " + id + "!");
            return false;
        }

        rawIcon = icon.clone();

        permission = get("permission").getString("hmcpets.pet." + id);

        useDefaultFollowAlgorithm = get("use-default-follow-algorithm").getBoolean(true);

        for(CommentedConfigurationNode node : get("skins").childrenMap().values()) {
            String skinID = node.key().toString();

            Material skinIconMat = Material.getMaterial(node.node("skin-icon").getString("STONE"));

            SkinModel skin = new SkinModel(skinID,
                    node.node("skin-id").getString(),
                    HMCPets.instance().mobTypeRegistry().getRegistered(node.node("skin-type").getString().toLowerCase()).orElseThrow(NoSuchFieldError::new),
                    (skinIconMat == null) ? Hooks.getItem(node.node("skin-icon").getString("STONE")) : new ItemStack(skinIconMat));

            System.out.println("Loaded skin " + skinID);
            skins.put(skinID, skin);
        }

        for(CommentedConfigurationNode node : get("levels").childrenMap().values()){
            try{
                int level = Integer.parseInt(node.key().toString());

                CommentedConfigurationNode levelNode = get("levels").node(level);
                CommentedConfigurationNode mmNode = levelNode.node("mythicmobs");

                System.out.println("Level " + level);
                PetLevelData levelData = new PetLevelData(
                        levelNode.node("lore").getList(String.class),
                        levelNode.node("health").getInt(100),
                        levelNode.node("hunger").getInt(100),
                        level,
                        levelNode.node("exp-required").getInt(100),
                        mmNode.node("tick-skill").getString(""),
                        mmNode.node("spawn-skill").getString(""),
                        mmNode.node("death-skill").getString("")
                );
                System.out.println(levelData);
                levels.put(level, levelData);
            } catch (NumberFormatException | SerializationException e){
                System.out.println("Malformed config (level " + node.key().toString() + "): " + id + ". Aborting loading this pet data!");
                return false;
            }
        }

	    System.out.println("Loaded " + id + " pet (" + type.id() + ", icon: " + icon.getType().name() + ")");
        return true;
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