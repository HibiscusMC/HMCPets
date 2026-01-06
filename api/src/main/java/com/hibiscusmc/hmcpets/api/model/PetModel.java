package com.hibiscusmc.hmcpets.api.model;

import com.hibiscusmc.hmcpets.api.HMCPets;
import com.hibiscusmc.hmcpets.api.data.IPetData;
import com.hibiscusmc.hmcpets.api.data.IPetLevelData;
import com.hibiscusmc.hmcpets.api.event.PetDespawnEvent;
import com.hibiscusmc.hmcpets.api.event.PetSpawnEvent;
import com.hibiscusmc.hmcpets.api.model.pathfinding.PetFollowGoal;
import com.hibiscusmc.hmcpets.api.model.registry.PetRarity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftLivingEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.UUID;

@Data
@EqualsAndHashCode(exclude = "owner")
public class PetModel {

    private static final Logger log = LoggerFactory.getLogger(PetModel.class);
    private final UUID id;

    @ToString.Exclude
    private final UserModel owner;
    private final IPetData config;

    private String name;
    private int level;
    private long experience;

    private LivingEntity entity;
    private Player ownerInstance;

    private SkinModel skin;
    private PetRarity rarity = PetRarity.COMMON;
    private CollarModel collar;
    private ItemStack craving;


    //
    private long obtainedTimestamp = -1;
    private long lastFed;

    private int power;
    private double health;
    private double attack;
    private double hunger;

    private boolean favorite;


    protected PetModel(UUID id, UserModel owner, IPetData config){
        this.id = id;
        this.owner = owner;
        this.config = config;

        //Beautify name
	    this.name = Character.toUpperCase(config.id().charAt(0)) + config.id().substring(1).replace("_", " ");

        obtainedTimestamp = System.currentTimeMillis();
        lastFed = System.currentTimeMillis();
    }

    protected PetModel(UUID id, UserModel owner, IPetData config, long obtainedTimestamp){
        this.id = id;
        this.owner = owner;
        this.config = config;

        //Beautify name
        this.name = Character.toUpperCase(config.id().charAt(0)) + config.id().substring(1).replace("_", " ");

        obtainedTimestamp = obtainedTimestamp;
        lastFed = System.currentTimeMillis();
    }


    public void spawn(Location location){
        //Spawn the mob via the MobType
        entity(config().mobType().spawn(config().mobID(), location));

        //Catch errors whilst spawning
        if(entity == null){
            return;
        }

        retrieveOwnerInstance();

        Mob mob = (Mob)entity;
        mob.setAI(true);
        mob.setAware(true);
        mob.customName(Component.text(name()));
        mob.setCustomNameVisible(true);

        mob.getPersistentDataContainer().set(HMCPets.getInstance().PET_ID_KEY, PersistentDataType.STRING, id.toString());
        mob.getPersistentDataContainer().set(HMCPets.getInstance().PET_OWNER_KEY, PersistentDataType.STRING, ownerInstance.getUniqueId().toString());

        config().mobType().addNameplate(entity(), Component.text(name()).appendNewline().append(Component.text(ownerInstance.getName() + "'s pet")));

        if(config.useDefaultFollowAlgorithm()){
            setupFollowOwner();
        }

        PetSpawnEvent event = new PetSpawnEvent(ownerInstance(), this);
        if(!event.isCancelled()) return;

        //Do not call the event - if canceled too could lead to strange behavior
        despawn(false);
    }

    public void despawn(boolean callEvent){
        if(!isSpawned()) return;

        if(callEvent){
            PetDespawnEvent event = new PetDespawnEvent(ownerInstance(), this);
            if(event.isCancelled()) return;
        }

        ownerInstance(null);

        if(entity() != null){
            System.out.println("Destroying pet model");
            //Remove le entity and le nameplate
            config().mobType().removeNameplate(entity());

            entity().remove();
            entity(null);
        }
    }

    public void destroy(){
        if(!isSpawned()) return;

        config().mobType().removeNameplate(entity());
        entity().remove();
    }


    //TODO: Resting feature
    public boolean isResting(){
        return false;
    }

    private void retrieveOwnerInstance(){
        //Entity is spawned - look if we have the owner already cached, if not, cache em.
        ownerInstance = Bukkit.getPlayer(owner.uuid());

        //If the owner is still null, despawn this entity.
        if(ownerInstance == null){
            despawn(false);
            return;
        }
    }

    public int expToNextLevel(){
        Optional<IPetLevelData> levelData = config.getLevel(level);
        if(levelData.isEmpty()){
            Bukkit.getLogger().severe("A pet (" + config.id() + ") is currently on a level (" + level + ") which is undefined!");
            return Integer.MAX_VALUE;
        }

        return Math.toIntExact(levelData.get().expToNextLevel() - experience);
    }

    public void levelUp(){
        if(level() >= config().maxLevel()) return;

        level(level + 1);
        experience(0);

        if(ownerInstance != null){
            ownerInstance.showTitle(Title.title(Component.text("Pet leveled up!"), Component.text(name() + " leveled up to Level " + level() + "!")));
        }
    }

    public boolean isSpawned(){
        return entity != null;
    }

    /**
     * Determines if the pet can be sold based on the way it was obtained through.
     *
     * @return true if the pet was not obtained via permissibles (cannot sell those)
     */
    public boolean isObtainedViaPerms() { return obtainedTimestamp != -1; }

    private void setupFollowOwner(){
        /*net.minecraft.world.entity.LivingEntity craftLivingEntity = ((CraftLivingEntity)entity).getHandle();

        craftLivingEntity.getBrain().removeAllBehaviors();
        craftLivingEntity.getBrain().addActivity(Activity.CORE, 0, ImmutableList.of(new PetFollowBehavior(ownerInstance, 1.2, 3, 10)));
        craftLivingEntity.getBrain().setActiveActivityIfPossible(Activity.CORE);*/

        net.minecraft.world.entity.Mob nmsEntity = (net.minecraft.world.entity.Mob) ((CraftLivingEntity)entity).getHandle();

        //Manage goals
        //TODO: Sprint review showcase - this doesn't work on all mobs (some use Brain API) and is not scalable!
        //TODO: PetFollowGoal only works for ground-based mobs
        //TODO: Make PetFollowGoal specific for PetType and make PetType goals extensible.
        nmsEntity.goalSelector.removeAllGoals(goal -> true);
        nmsEntity.targetSelector.removeAllGoals(goal -> true);

        nmsEntity.goalSelector.addGoal(1, new PetFollowGoal(nmsEntity, ownerInstance, 1.2, 3, 10));
    }

    public static PetModel fromPermissible(UUID id, UserModel owner, IPetData config){
        return new PetModel(id, owner, config);
    }

    public static PetModel fromPermissible(UserModel owner, IPetData config){
        return new PetModel(UUID.randomUUID(), owner, config);
    }

    public static PetModel of(UUID id, UserModel owner, IPetData config){
        return new PetModel(id, owner, config, System.currentTimeMillis());
    }

    public static PetModel of(UserModel owner, IPetData config){
        return new PetModel(UUID.randomUUID(), owner, config, System.currentTimeMillis());
    }
}