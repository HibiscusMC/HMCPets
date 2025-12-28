package com.hibiscusmc.hmcpets.listener;

import com.hibiscusmc.hmcpets.cache.UserCache;
import lombok.extern.slf4j.Slf4j;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import team.unnamed.inject.Inject;

@Slf4j
public class PlayerJoinListener implements Listener {

    @Inject
    private UserCache userCache;

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        userCache.fetch(event.getPlayer().getUniqueId());
    }

}
