package com.hibiscusmc.hmcpets.listener;

import com.hibiscusmc.hmcpets.api.HMCPets;
import com.hibiscusmc.hmcpets.api.model.UserModel;
import com.hibiscusmc.hmcpets.cache.UserCache;
import me.lojosho.hibiscuscommons.api.events.HibiscusHooksAllActiveEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import team.unnamed.inject.Inject;

public class PetOwnerDisconnectListener implements Listener {

	@Inject
	private HMCPets instance;

	@Inject
	private UserCache userCache;

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		userCache.fetch(event.getPlayer().getUniqueId()).thenAccept(UserModel::despawnActivePets);
	}

}
