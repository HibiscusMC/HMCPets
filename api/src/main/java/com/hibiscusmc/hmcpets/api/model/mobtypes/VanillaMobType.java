package com.hibiscusmc.hmcpets.api.model.mobtypes;

import com.hibiscusmc.hmcpets.api.model.PetModel;
import com.hibiscusmc.hmcpets.api.model.registry.MobType;
import com.hibiscusmc.hmcpets.api.registry.Registry;
import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.util.Transformation;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class VanillaMobType extends MobType {

    public VanillaMobType() {
        super(Registry.withDefaultKey("vanilla"));
    }

    @Override
    public LivingEntity spawn(String id, Location loc) {
        EntityType mobType = EntityType.fromName(id);
        if(mobType == null){
            return null;
        }

        return (LivingEntity) loc.getWorld().spawnEntity(loc, mobType);
    }

    @Override
    public void despawn(Object entity) {
        if(!(entity instanceof LivingEntity le)) return;

        le.remove();
    }

    @Override
    public void tick(Object entity, PetModel pet) {
        //Nothing to do
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
