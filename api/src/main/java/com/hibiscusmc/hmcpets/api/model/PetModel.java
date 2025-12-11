package com.hibiscusmc.hmcpets.api.model;

import com.google.common.collect.ImmutableList;
import com.hibiscusmc.hmcpets.api.data.IPetData;
import com.hibiscusmc.hmcpets.api.model.pathfinding.PetFollowBehavior;
import com.hibiscusmc.hmcpets.api.model.pathfinding.PetFollowGoal;
import com.hibiscusmc.hmcpets.api.model.registry.PetRarity;
import com.hibiscusmc.hmcpets.api.model.enums.PetStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import me.lojosho.hibiscuscommons.hooks.Hooks;
import me.lojosho.hibiscuscommons.hooks.items.HookMythic;
import me.lojosho.hibiscuscommons.hooks.misc.HookModelEngine;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.util.TriState;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftLivingEntity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.List;
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

    private LivingEntity entity;
    private Player ownerInstance;

    private SkinModel skin;
    private PetRarity rarity;
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

        }else{
            try{
                entity((LivingEntity) location.getWorld().spawnEntity(location, mobType));
            }catch (Exception e){
                warn("Error whilst spawning mob " + name());
            }
        }

        retrieveOwnerInstance();

        Mob mob = (Mob)entity;
        mob.setAI(true);
        mob.setAware(true);
        mob.customName(Component.text("workyoufuckingbitch"));
        mob.setCustomNameVisible(true);

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

        nmsEntity.goalSelector.addGoal(1, new PetFollowGoal(nmsEntity, ownerInstance, 1.2, 3));
    }

    public void despawn(){
        if(entity() == null) return;

        entity().remove();
        entity(null);
    }

    private void retrieveOwnerInstance(){
        //Entity is spawned - look if we have the owner already cached, if not, cache em.
        if(ownerInstance == null){
            ownerInstance = Bukkit.getPlayer(owner.uuid());

            //If the owner is still null, despawn this entity.
            if(ownerInstance == null){
                despawn();
                return;
            }
        }
    }

    public boolean isSpawned(){
        return entity != null;
    }

}