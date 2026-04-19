package com.hibiscusmc.hmcpets.listener;

import com.hibiscusmc.hmcpets.api.event.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class NametagUpdaterListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPetSpawn(PetSpawnEvent event) {
        event.pet().updateNametag(String.join("\n", event.pet().config().nametag()));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPetHPLost(PetHPLostEvent event) {
        if(!event.pet().isSpawned()) return;

        event.pet().updateNametag(String.join("\n", event.pet().config().nametag()));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPetHPGain(PetHPGainedEvent event) {
        if(!event.pet().isSpawned()) return;

        event.pet().updateNametag(String.join("\n", event.pet().config().nametag()));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPetExp(PetExpChangedEvent event) {
        if(!event.pet().isSpawned()) return;

        event.pet().updateNametag(String.join("\n", event.pet().config().nametag()));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPetNameChange(PetNameChangeEvent event) {
        if(!event.pet().isSpawned()) return;

        event.pet().updateNametag(String.join("\n", event.pet().config().nametag()));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onHungerLost(PetHungerLostEvent event) {
        if(!event.pet().isSpawned()) return;

        event.pet().updateNametag(String.join("\n", event.pet().config().nametag()));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onHungerGain(PetHungerGainedEvent event) {
        if(!event.pet().isSpawned()) return;

        event.pet().updateNametag(String.join("\n", event.pet().config().nametag()));
    }

}