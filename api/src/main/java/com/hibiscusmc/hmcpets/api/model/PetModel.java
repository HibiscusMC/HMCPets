package com.hibiscusmc.hmcpets.api.model;

import com.hibiscusmc.hmcpets.api.HMCPets;
import com.hibiscusmc.hmcpets.api.data.IPetData;
import com.hibiscusmc.hmcpets.api.event.PetDespawnEvent;
import com.hibiscusmc.hmcpets.api.event.PetSpawnEvent;
import com.hibiscusmc.hmcpets.api.model.enums.PetStatus;
import com.hibiscusmc.hmcpets.api.model.pathfinding.PetFollowGoal;
import com.hibiscusmc.hmcpets.api.model.registry.PetRarity;
import com.hibiscusmc.hmcpets.api.util.hooks.ModelEngineHook;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import me.lojosho.hibiscuscommons.hooks.Hooks;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftLivingEntity;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

import static org.slf4j.helpers.Reporter.warn;

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

    private TextDisplay nameDisplay;
    private LivingEntity entity;
    private Player ownerInstance;

    private SkinModel skin;
    private PetRarity rarity = PetRarity.COMMON;
    private CollarModel collar;
    private ItemStack craving;

    private long obtainedTimestamp;
    private long lastFed;

    private PetStatus status;
    private int power;
    private double health;
    private double attack;
    private double hunger;


    public PetModel(UUID id, UserModel owner, IPetData config){
        this.id = id;
        this.owner = owner;
        this.config = config;

        //Beautify name
	    this.name = Character.toUpperCase(config.id().charAt(0)) + config.id().substring(1).replace("_", " ");
    }


    public void spawn(Location location){
        EntityType mobType = EntityType.fromName(config.mobType());
        //Get if ModelEngine is enabled
        if(Hooks.isActiveHook("ModelEngine") && mobType == null){
            //Spawn with ModelEngine
            entity(ModelEngineHook.spawnEntity(config.mobType(), location));
        }else{
            try{
                entity((LivingEntity) location.getWorld().spawnEntity(location, mobType));
            }catch (Exception e){
                warn("Error whilst spawning mob " + name());
            }
        }

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

        nameDisplay(mob.getWorld().spawn(mob.getLocation().add(0, 2, 0), TextDisplay.class));
        nameDisplay().setBillboard(Display.Billboard.CENTER);
        nameDisplay().setBackgroundColor(Color.fromARGB(0, 0, 0, 0));
        nameDisplay().text(Component.text(name()).appendNewline().append(Component.text(ownerInstance.getName() + "'s pet")));

        nameDisplay().setTransformation(new Transformation(
                new Vector3f(0, 1f, 0),
                new Quaternionf(),
                new Vector3f(1, 1, 1),
                new Quaternionf()
        ));

        mob.addPassenger(nameDisplay());

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

        nmsEntity.goalSelector.addGoal(1, new PetFollowGoal(nmsEntity, ownerInstance, 1.2, 3, 10, nameDisplay()));

        PetSpawnEvent event = new PetSpawnEvent(ownerInstance(), this);
        if(!event.isCancelled()) return;

        //Do not call the event - if canceled too could lead to strange behavior
        despawn(false);
    }

    public void despawn(boolean callEvent){
        if(callEvent){
            PetDespawnEvent event = new PetDespawnEvent(ownerInstance(), this);
            if(event.isCancelled()) return;
        }

        ownerInstance(null);

        Bukkit.getScheduler().scheduleSyncDelayedTask(HMCPets.instance(), () -> {
            if(entity() != null){
                System.out.println("Destroying pet model");
                //Remove le entity
                entity().remove();
                entity(null);
            }

            if(nameDisplay() != null){
                System.out.println("Destroying name model");
                //Remove le name
                nameDisplay().remove();
                nameDisplay(null);
            }
        });
    }

    public void destroy(){
        entity().remove();
        nameDisplay().remove();
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

    public boolean isSpawned(){
        return entity != null;
    }

}