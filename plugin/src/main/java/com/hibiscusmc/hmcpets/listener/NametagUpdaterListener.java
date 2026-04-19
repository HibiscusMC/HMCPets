package com.hibiscusmc.hmcpets.listener;

import com.hibiscusmc.hmcpets.api.event.*;
import com.hibiscusmc.hmcpets.config.LangConfig;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import team.unnamed.inject.Inject;

public class NametagUpdaterListener implements Listener {

    @Inject
    private LangConfig langConfig;

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPetSpawn(PetSpawnEvent event) {
        event.pet().updateNametag(langConfig.petsNametag().string());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPetHPLost(PetHPLostEvent event) {
        if(!event.pet().isSpawned()) return;

        event.pet().updateNametag(langConfig.petsNametag().string());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPetHPGain(PetHPGainedEvent event) {
        if(!event.pet().isSpawned()) return;

        event.pet().updateNametag(langConfig.petsNametag().string());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPetExp(PetExpChangedEvent event) {
        if(!event.pet().isSpawned()) return;

        event.pet().updateNametag(langConfig.petsNametag().string());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPetNameChange(PetNameChangeEvent event) {
        if(!event.pet().isSpawned()) return;

        event.pet().updateNametag(langConfig.petsNametag().string());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onHungerLost(PetHungerLostEvent event) {
        if(!event.pet().isSpawned()) return;

        event.pet().updateNametag(langConfig.petsNametag().string());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onHungerGain(PetHungerGainedEvent event) {
        if(!event.pet().isSpawned()) return;

        event.pet().updateNametag(langConfig.petsNametag().string());
    }

}