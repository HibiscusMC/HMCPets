package com.hibiscusmc.hmcpets.api.model.mobtypes;

import com.hibiscusmc.hmcpets.api.model.registry.MobType;
import com.hibiscusmc.hmcpets.api.registry.Registry;
import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.ActiveMob;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Zombie;

public class MythicMobsMobType extends MobType {

    public MythicMobsMobType() {
        super(Registry.withDefaultKey("mythicmobs"));
    }

    @Override
    public LivingEntity spawn(String id, Location loc) {
        try(MythicBukkit inst = MythicBukkit.inst()){
            MythicMob mob = inst.getMobManager().getMythicMob(id).orElse(null);
            if(mob == null) return null;

            //Spawn the actual mob & get its Bukkit handle
            ActiveMob mmMobInstance = mob.spawn(BukkitAdapter.adapt(loc),1);

            mmMobInstance.setDisplayName("");

            LivingEntity entity = (LivingEntity) mmMobInstance.getEntity().getBukkitEntity();

            //Prevent ZOMBIE types from burning in daylight
            if(entity instanceof Zombie zombie) {
                zombie.setShouldBurnInDay(false);
            }

            return entity;
        }
    }

    @Override
    public void addNameplate(Object mobInstance, Component text) {
        if(!(mobInstance instanceof LivingEntity mob)) return;

        setDisplayName(mob, text);
    }

    @Override
    public void editNameplate(Object mobInstance, Component newText) {
        if(!(mobInstance instanceof LivingEntity mob)) return;

        setDisplayName(mob, newText);
    }

    @Override
    public void removeNameplate(Object mobInstance) {
        if(!(mobInstance instanceof LivingEntity mob)) return;

        setDisplayName(mob, Component.empty());
    }

    private void setDisplayName(LivingEntity mob, Component text){
        try(MythicBukkit inst = MythicBukkit.inst()){
            inst.getMobManager().getActiveMob(mob.getUniqueId()).ifPresent(activeMob -> {
                activeMob.setDisplayName(LegacyComponentSerializer.legacySection().serialize(text));
            });
        }
    }
}
