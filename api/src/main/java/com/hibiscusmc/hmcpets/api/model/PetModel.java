package com.hibiscusmc.hmcpets.api.model;

import com.google.common.collect.ImmutableList;
import com.hibiscusmc.hmcpets.api.HMCPets;
import com.hibiscusmc.hmcpets.api.data.IPetData;
import com.hibiscusmc.hmcpets.api.data.IPetLevelData;
import com.hibiscusmc.hmcpets.api.data.IPluginData;
import com.hibiscusmc.hmcpets.api.event.*;
import com.hibiscusmc.hmcpets.api.model.pathfinding.PetFollowBehavior;
import com.hibiscusmc.hmcpets.api.model.pathfinding.PetFollowGoal;
import com.hibiscusmc.hmcpets.api.model.registry.MobType;
import com.hibiscusmc.hmcpets.api.model.registry.PetRarity;
import com.hibiscusmc.hmcpets.api.util.Adventure;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.title.Title;
import net.minecraft.world.entity.schedule.Activity;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftLivingEntity;
import org.bukkit.entity.EntityType;
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

    private final IPluginData pluginData;

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


    protected PetModel(UUID id, UserModel owner, IPetData config, IPluginData pluginData){
        this.id = id;
        this.owner = owner;
        this.config = config;
        this.pluginData = pluginData;

        //Beautify name
	    this.name = Character.toUpperCase(config.id().charAt(0)) + config.id().substring(1).replace("_", " ");

        this.obtainedTimestamp = System.currentTimeMillis();
        lastFed = System.currentTimeMillis();
    }

    protected PetModel(UUID id, UserModel owner, IPetData config, IPluginData pluginData, long obtainedTimestamp){
        this.id = id;
        this.owner = owner;
        this.config = config;
        this.pluginData = pluginData;

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
        mob.setCustomNameVisible(true);

        mob.getPersistentDataContainer().set(HMCPets.getInstance().PET_ID_KEY, PersistentDataType.STRING, id.toString());
        mob.getPersistentDataContainer().set(HMCPets.getInstance().PET_OWNER_KEY, PersistentDataType.STRING, ownerInstance.getUniqueId().toString());

        if(config.useDefaultFollowAlgorithm()){
            setupFollowOwner();
        }

        PetSpawnEvent event = new PetSpawnEvent(ownerInstance(), this);
        Bukkit.getPluginManager().callEvent(event);
        if(!event.isCancelled()) return;

        //Do not call the event - if canceled too could lead to strange behavior
        despawn(false);
    }

    public void despawn(boolean callEvent){
        if(!isSpawned()) return;

        if(callEvent){
            PetDespawnEvent event = new PetDespawnEvent(ownerInstance(), this);
            Bukkit.getPluginManager().callEvent(event);
            if(event.isCancelled()) return;
        }

        ownerInstance(null);

        if(entity() != null){
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
        return health() <= 0;
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
        Bukkit.getPluginManager().callEvent(event);
        if(event.isCancelled()) return;

        health -= event.amount();
        if(health <= 0){
            ownerInstance.sendMessage(Component.text("Your pet " + name() +  " was downed!"));
            despawn(true);
            return;
        }
    }

    public void regainHP(double amount){
        int maxHealth = maxHealth();
        if(health >= maxHealth) return;

        PetHPGainedEvent event = new PetHPGainedEvent(this, health + amount > maxHealth ? maxHealth - health : amount);
        Bukkit.getPluginManager().callEvent(event);
        if(event.isCancelled()) return;

        health += event.amount();
        if(health > maxHealth) health = maxHealth;
    }

    public void spendFood(double amount){
        if(!isSpawned()) return;

        PetHungerLostEvent event = new PetHungerLostEvent(this, amount);
        Bukkit.getPluginManager().callEvent(event);
        if(event.isCancelled()) return;

        hunger -= event.amount();
        if(hunger <= 0){
            ownerInstance.sendMessage(Component.text("Your pet " + name() +  " was downed (Too hungry!)"));
            despawn(true);
            return;
        }
    }

    public void regainHunger(double amount){
        int maxHunger = maxHunger();
        if(hunger >= maxHunger) return;

        PetHungerGainedEvent event = new PetHungerGainedEvent(this, hunger + amount > maxHunger ? maxHunger - hunger : amount);
        Bukkit.getPluginManager().callEvent(event);
        if(event.isCancelled()) return;

        hunger += event.amount();
        if(hunger > maxHunger) hunger = maxHunger;
    }

    public void updateNametag(String nameplateTemplate){
        Bukkit.getScheduler().runTaskLaterAsynchronously(HMCPets.getInstance(), () -> {
            config().mobType().editNameplate(entity(), Adventure.parseForMeta(nameplateTemplate, TagResolver.builder()
                    .tag("pet", (arg, context) -> Tag.inserting(Component.text(name())))
                    .tag("healthbar", (arg, context) -> Tag.inserting(Component.text(pluginData.glyphs().healthBar().getSegment((int) health(), maxHealth()))))
                    .tag("hungerbar", (arg, context) -> Tag.inserting(Component.text(pluginData.glyphs().hungerBar().getSegment((int) hunger(), maxHunger()))))
                    .tag("hp", (arg, context) -> Tag.inserting(Component.text(health())))
                    .tag("maxhp", (arg, context) -> Tag.inserting(Component.text(maxHealth())))
                    .tag("hunger", (arg, context) -> Tag.inserting(Component.text(hunger())))
                    .tag("maxhunger", (arg, context) -> Tag.inserting(Component.text(maxHunger())))
                    .build()));
        }, 2L);
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
        if(entity.getType() == EntityType.VILLAGER){
            net.minecraft.world.entity.LivingEntity craftLivingEntity = ((CraftLivingEntity)entity).getHandle();

            craftLivingEntity.getBrain().removeAllBehaviors();
            craftLivingEntity.getBrain().addActivity(Activity.CORE, 0, ImmutableList.of(new PetFollowBehavior(ownerInstance, 1.2, 3)));
            craftLivingEntity.getBrain().setActiveActivityIfPossible(Activity.CORE);

            return;
        }

        net.minecraft.world.entity.Mob nmsEntity = (net.minecraft.world.entity.Mob) ((CraftLivingEntity)entity).getHandle();

        nmsEntity.goalSelector.removeAllGoals(goal -> true);
        nmsEntity.targetSelector.removeAllGoals(goal -> true);

        nmsEntity.goalSelector.addGoal(1, new PetFollowGoal(nmsEntity, ownerInstance, 1.2, 3, 10));
    }

    public void experience(long experience){
        if(isSpawned()){
            PetExpChangedEvent event = new PetExpChangedEvent(this, experience);
            Bukkit.getPluginManager().callEvent(event);
            if(event.isCancelled()) return;

            this.experience = event.amount();
            return;
        }

        this.experience = experience;
    }

    public void name(String name){
        if(isSpawned()){
            PetNameChangeEvent event = new PetNameChangeEvent(this, name);
            Bukkit.getScheduler().scheduleSyncDelayedTask(HMCPets.getInstance(), () -> {
                Bukkit.getPluginManager().callEvent(event);
            });
        }

        this.name = name;
    }

    public static PetModel fromPermissible(UUID id, UserModel owner, IPetData config, IPluginData pluginData){
        return new PetModel(id, owner, config, pluginData);
    }

    public static PetModel fromPermissible(UserModel owner, IPetData config, IPluginData pluginData){
        return new PetModel(UUID.randomUUID(), owner, config, pluginData);
    }

    public static PetModel of(UUID id, UserModel owner, IPetData config, IPluginData pluginData){
        return new PetModel(id, owner, config, pluginData, System.currentTimeMillis());
    }

    public static PetModel of(UserModel owner, IPetData config, IPluginData pluginData){
        return new PetModel(UUID.randomUUID(), owner, config, pluginData, System.currentTimeMillis());
    }
}