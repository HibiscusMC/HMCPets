package com.hibiscusmc.hmcpets.api.model;

import com.hibiscusmc.hmcpets.api.HMCPets;
import com.hibiscusmc.hmcpets.api.data.IPetData;
import com.hibiscusmc.hmcpets.api.data.IPetLevelData;
import com.hibiscusmc.hmcpets.api.event.*;
import com.hibiscusmc.hmcpets.api.model.pathfinding.PetFollowGoal;
import com.hibiscusmc.hmcpets.api.model.registry.MobType;
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

import java.util.Comparator;
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
    private long experience;

    private LivingEntity entity;
    private Player ownerInstance;

    private SkinModel skin;
    private PetRarity rarity = PetRarity.COMMON;
    private CollarModel collar;
    private ItemStack craving;

    private long obtainedTimestamp = -1;
    private long lastFed;

    private int power;
    private double health = 5;
    private double attack;
    private double hunger = 5;

    private boolean favorite;


    protected PetModel(UUID id, UserModel owner, IPetData config){
        this.id = id;
        this.owner = owner;
        this.config = config;

        //Beautify name
	    this.name = Character.toUpperCase(config.id().charAt(0)) + config.id().substring(1).replace("_", " ");

        this.obtainedTimestamp = System.currentTimeMillis();
        lastFed = System.currentTimeMillis();
    }

    protected PetModel(UUID id, UserModel owner, IPetData config, long obtainedTimestamp){
        this.id = id;
        this.owner = owner;
        this.config = config;

        //Beautify name
        this.name = Character.toUpperCase(config.id().charAt(0)) + config.id().substring(1).replace("_", " ");

        this.obtainedTimestamp = obtainedTimestamp;
        lastFed = System.currentTimeMillis();
    }


    public void spawn(Location location){
        String finalMobId = skin == null ? config.mobID() : skin.mobId();
        MobType finalMobType = skin == null ? config.mobType() : skin.mobType();

        //Spawn the mob via the MobType
        entity(finalMobType.spawn(finalMobId, location));

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

        updateNametag();

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

            MobType mobType = skin == null ? config.mobType() : skin.mobType();

            mobType.removeNameplate(entity());
            mobType.despawn(entity());
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
        int nextLevel = config.levels().keySet().stream().filter(i -> i > level()).min(Integer::compareTo).orElse(Integer.MAX_VALUE);
        if(nextLevel == Integer.MAX_VALUE){
            return Integer.MAX_VALUE;
        }

        Optional<IPetLevelData> levelData = config.getLevel(nextLevel);
        if(levelData.isEmpty()){
            Bukkit.getLogger().severe("A pet (" + config.id() + ") is currently on a level (" + level() + ") which is undefined!");
            return Integer.MAX_VALUE;
        }

        System.out.println("Current exp: " + experience);
        System.out.println("Next level exp: " + levelData.get().expRequired());
        return Math.toIntExact(levelData.get().expRequired() - experience);
    }

    public Optional<IPetLevelData> getNextLevelData(){
        int nextLevel = config.levels().keySet().stream().filter(i -> i > level()).min(Integer::compareTo).orElse(Integer.MAX_VALUE);
        if(nextLevel == Integer.MAX_VALUE){
            return Optional.empty();
        }

        return config.getLevel(nextLevel);
    }

    public void levelUp(){
        if(level() >= config().maxLevel()) return;

        if(ownerInstance != null){
            ownerInstance.showTitle(Title.title(Component.text("Pet leveled up!"), Component.text(name() + " leveled up to Level " + level() + "!")));
        }
    }

    public Optional<IPetLevelData> getLevelData(){
        return config().getLevel(level());
    }

    public int level(){
        Optional<IPetLevelData> levelData = config().levels().values().stream().filter(data -> data.expRequired() <= experience).max(Comparator.comparingInt(IPetLevelData::level));
        return levelData.map(IPetLevelData::level).orElse(0);
    }

    public void skin(SkinModel skin){
        skin(skin.id());
    }

    public void skin(String skinID){
        if(skinID == null) {
            skin = null;
            respawn();
            return;
        }

        SkinModel skinModel = config.skins().get(skinID);
        if(skinModel == null){
            respawn();
            return;
        }

        skin = skinModel;
        if(!isSpawned()) return;

        respawn();
    }

    public void respawn(){
        Location currentLocation = entity().getLocation().clone();
        destroy();

        spawn(currentLocation);
    }

    public int maxHunger(){
        return getLevelData().map(IPetLevelData::maxHunger).orElse(-1);
    }

    public int maxHealth(){
        return getLevelData().map(IPetLevelData::maxHealth).orElse(-1);
    }

    public void hurt(double amount){
        if(!isSpawned()) return;

        PetHPLostEvent event = new PetHPLostEvent(this, amount);
        if(event.isCancelled()) return;

        health -= event.amount();
        if(health <= 0){
            ownerInstance.sendMessage(Component.text("Your pet " + name() +  " was downed!"));
            despawn(true);
            return;
        }

        updateNametag();
    }

    public void regainHP(double amount){
        int maxHealth = maxHealth();
        if(health >= maxHealth) return;

        PetHPGainedEvent event = new PetHPGainedEvent(this, health + amount > maxHealth ? maxHealth - health : amount);
        if(event.isCancelled()) return;

        health += event.amount();
        if(health > maxHealth) health = maxHealth;

        if(isSpawned()) updateNametag();
    }

    public void spendFood(double amount){
        if(!isSpawned()) return;

        PetHungerLostEvent event = new PetHungerLostEvent(this, amount);
        if(event.isCancelled()) return;

        hunger -= event.amount();
        if(hunger <= 0){
            ownerInstance.sendMessage(Component.text("Your pet " + name() +  " was downed (Too hungry!)"));
            despawn(true);
            return;
        }

        updateNametag();
    }

    public void regainHunger(double amount){
        int maxHunger = maxHunger();
        if(hunger >= maxHunger) return;

        PetHungerGainedEvent event = new PetHungerGainedEvent(this, hunger + amount > maxHunger ? maxHunger - hunger : amount);
        if(event.isCancelled()) return;

        hunger += event.amount();
        if(hunger > maxHunger) hunger = maxHunger;

        if(isSpawned()) updateNametag();
    }

    public void updateNametag(){
        config().mobType().editNameplate(entity(),
                Component.text(name())
                        .appendNewline()
                        .append(Component.text(ownerInstance.getName() + "'s pet"))
                        .appendNewline()
                        .append(Component
                                .text("HP - " + health  + " / " + maxHealth() + ", Food - " + hunger  + " / " + maxHunger())));

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

    public void experience(long experience){
        PetExpChangedEvent event = new PetExpChangedEvent(this, experience);
        if(event.isCancelled()) return;

        this.experience = event.amount();
    }

    public void name(String name){
        this.name = name;

        //Update nametag if spawned
        if(!isSpawned()) return;
        config().mobType().editNameplate(entity(), Component.text(name).appendNewline().append(Component.text(ownerInstance.getName() + "'s pet")));
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