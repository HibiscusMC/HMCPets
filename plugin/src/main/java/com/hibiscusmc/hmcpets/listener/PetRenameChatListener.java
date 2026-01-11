package com.hibiscusmc.hmcpets.listener;

import com.hibiscusmc.hmcpets.api.HMCPets;
import com.hibiscusmc.hmcpets.api.model.PetModel;
import com.hibiscusmc.hmcpets.api.model.UserModel;
import com.hibiscusmc.hmcpets.cache.UserCache;
import com.hibiscusmc.hmcpets.config.MenuConfig;
import com.hibiscusmc.hmcpets.config.PetConfig;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import team.unnamed.inject.Inject;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PetRenameChatListener implements Listener {

    @Inject
    private PetConfig petConfig;
    @Inject
    private MenuConfig menuConfig;
    @Inject
    private HMCPets instance;

    @Inject
    private UserCache userCache;

    private static final Map<UUID, PetModel> awaitingRenames = new HashMap<>();


    @EventHandler
    public void onAllHooksReady(AsyncChatEvent event) {
        if(!awaitingRenames.containsKey(event.getPlayer().getUniqueId())) return;

        event.setCancelled(true);

        if(event.message().toString().contains("cancel")){
            awaitingRenames.remove(event.getPlayer().getUniqueId());
            event.getPlayer().sendMessage(Component.text("Canceled!"));
            return;
        }

        PetModel pet = awaitingRenames.get(event.getPlayer().getUniqueId());

        awaitingRenames.remove(event.getPlayer().getUniqueId());

        //Check if the player still owns the pet (we don't know how much time has passed)
        UserModel model = userCache.get(event.getPlayer().getUniqueId());
        if(model == null) return; //How?

        if(!model.pets().containsKey(pet.id())){
            return;
        }

        //Rename the pet
        pet.name(LegacyComponentSerializer.legacySection().serialize(event.message().color(null)));

        event.getPlayer().sendMessage(Component.text("Renamed!"));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event){
        awaitingRenames.remove(event.getPlayer().getUniqueId());
    }

    public static void addRenameReq(Player player, PetModel pet){
        awaitingRenames.put(player.getUniqueId(), pet);
    }

}
