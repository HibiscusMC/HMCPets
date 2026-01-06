package com.hibiscusmc.hmcpets.tasks;

import com.hibiscusmc.hmcpets.api.HMCPets;
import com.hibiscusmc.hmcpets.api.model.PetModel;
import com.hibiscusmc.hmcpets.api.model.UserModel;
import com.hibiscusmc.hmcpets.cache.UserCache;
import com.hibiscusmc.hmcpets.config.PetConfig;
import com.hibiscusmc.hmcpets.config.PluginConfig;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import team.unnamed.inject.Inject;

import java.util.List;

public class PermissibleTask implements Runnable{

    @Inject
    private UserCache userCache;

    @Inject
    private HMCPets instance;

    @Inject
    private PetConfig petConfig;

    @Inject
    private PluginConfig pluginConfig;

    @Override
    public void run() {
        Bukkit.getOnlinePlayers().forEach(p -> {
            UserModel cachedUser = userCache.get(p.getUniqueId());
            if(cachedUser == null) return; //User was not cached - they didn't spawn any pets or accessed the plugin yet.

            checkAddedPermissibles(cachedUser, p);
            checkRemovedPermissibles(cachedUser, p);
        });
    }


    private void checkAddedPermissibles(UserModel cachedUser, Player playerInstance){
        //Check for all pets configs, except the ones players already have (if they sell the one they have, it'll get caught by this check
        //when it happens, and they'll get the permissible-based one. If duplicate pets are allowed, fuck it and let's give it to them regardless.

        petConfig.allPets().values().forEach(petConfig -> {
            if(!playerInstance.hasPermission(petConfig.permission())) return;

            List<UserModel.CachedPet> matchingPets = cachedUser.pets().values().stream().filter(pet -> pet.pet().config().id().equals(petConfig.id())).toList();

            //Player already has the permissible pet, we're not gonna add anything
            if(matchingPets.stream().anyMatch(pet -> pet.pet().isObtainedViaPerms())) return;

            //If the player already has a pet of that type AND duplicate pets are not allowed, don't add it
            if(!matchingPets.isEmpty() && !pluginConfig.users().allowDuplicatePets()) return;

            cachedUser.addPet(PetModel.fromPermissible(cachedUser, petConfig));
            playerInstance.sendMessage(Component.text("You got the " + petConfig.id() + " pet!"));
        });
    }

    private void checkRemovedPermissibles(UserModel cachedUser, Player playerInstance){
        //Check for all pets whether the player still has perms - if not, despawn (if spawned) and remove them
        List<UserModel.CachedPet> spawnedPets = cachedUser.pets().values().stream().filter(pet -> pet.pet().isObtainedViaPerms()).toList(); //Can't do CD operations here

        spawnedPets.forEach(cachedPet -> {
            if(playerInstance.hasPermission(cachedPet.pet().config().permission())) return;

            if(cachedPet.pet().isSpawned()){
                Bukkit.getScheduler().scheduleSyncDelayedTask(instance, () -> cachedPet.pet().despawn(false)); //Pet despawning must be sync.
            }

            cachedUser.removePet(cachedPet.pet());
            playerInstance.sendMessage(Component.text("You lost ownership of your " + cachedPet.pet().name() + " pet!"));
        });
    }

}
