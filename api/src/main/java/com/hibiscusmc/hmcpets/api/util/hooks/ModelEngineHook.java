package com.hibiscusmc.hmcpets.api.util.hooks;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.ModeledEntity;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Pig;

public class ModelEngineHook {

    public static LivingEntity spawnEntity(String type, Location location){
       try{
           Pig entity = location.getWorld().spawn(location, Pig.class);

           // Hide and disable the base entity
           entity.setInvisible(true);
           entity.setAI(false);
           entity.setSilent(true);
           entity.setCollidable(false);
           entity.setInvulnerable(true);

           ModeledEntity modeledEntity = ModelEngineAPI.createModeledEntity(entity);

           ActiveModel activeModel = ModelEngineAPI.createActiveModel(type);
           modeledEntity.addModel(activeModel, true);

           return entity;
       }catch (Exception e){
           Bukkit.getLogger().warning(type + " mob doesn't exist! Aborting pet spawn!");
           return null;
       }
    }

}
