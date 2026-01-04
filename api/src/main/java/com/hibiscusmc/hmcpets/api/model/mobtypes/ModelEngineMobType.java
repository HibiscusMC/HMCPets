package com.hibiscusmc.hmcpets.api.model.mobtypes;

import com.hibiscusmc.hmcpets.api.model.registry.MobType;
import com.hibiscusmc.hmcpets.api.registry.Registry;
import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.ModeledEntity;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.util.Transformation;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class ModelEngineMobType extends MobType {

    public ModelEngineMobType() {
        super(Registry.withDefaultKey("modelengine"));
    }

    @Override
    public LivingEntity spawn(String id, Location loc) {
        try{
            Pig entity = loc.getWorld().spawn(loc, Pig.class);

            // Hide and disable the base entity
            entity.setInvisible(true);
            entity.setAI(false);
            entity.setSilent(true);
            entity.setCollidable(false);
            entity.setInvulnerable(true);

            ModeledEntity modeledEntity = ModelEngineAPI.createModeledEntity(entity);

            ActiveModel activeModel = ModelEngineAPI.createActiveModel(id);
            modeledEntity.addModel(activeModel, true);

            return entity;
        }catch (Exception e){
            Bukkit.getLogger().warning(id + " Mythicmob mob doesn't exist! Aborting pet spawn!");
            return null;
        }
    }

    @Override
    public void addNameplate(Object mobInstance, Component text) {
        if(!(mobInstance instanceof LivingEntity mob)) return;

        TextDisplay nameplate = mob.getWorld().spawn(mob.getLocation().add(0, 2, 0), TextDisplay.class);
        nameplate.setBillboard(Display.Billboard.CENTER);
        nameplate.setBackgroundColor(Color.fromARGB(0, 0, 0, 0));
        nameplate.text(text);

        nameplate.setTransformation(new Transformation(
                new Vector3f(0, 1f, 0),
                new Quaternionf(),
                new Vector3f(1, 1, 1),
                new Quaternionf()
        ));

        mob.addPassenger(nameplate);
    }

    @Override
    public void editNameplate(Object mobInstance, Component newText) {
        if(!(mobInstance instanceof LivingEntity mob)) return;

        if(mob.getPassengers().isEmpty()){
            addNameplate(mobInstance, newText);
            return;
        }

        if(mob.getPassengers().isEmpty()) return;
        for(Entity passenger : mob.getPassengers()){
            if(!(passenger instanceof TextDisplay nameplate)) continue;

            nameplate.text(newText);
            return;
        }

        addNameplate(mobInstance, newText);
    }

    @Override
    public void removeNameplate(Object mobInstance) {
        if(!(mobInstance instanceof LivingEntity mob)) return;

        if(mob.getPassengers().isEmpty()) return;
        for(Entity passenger : mob.getPassengers()){
            if(!(passenger instanceof TextDisplay nameplate)) continue;

            nameplate.remove();
        }
    }
}
