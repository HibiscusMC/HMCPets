package com.hibiscusmc.hmcpets.util.hooks;

import com.hibiscusmc.hmcpets.api.HMCPets;
import com.hibiscusmc.hmcpets.api.model.UserModel;
import com.hibiscusmc.hmcpets.cache.UserCache;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import team.unnamed.inject.Inject;

public class PlaceholderAPIHook extends PlaceholderExpansion {

    @Inject
    private HMCPets instance;

    @Inject
    private UserCache userCache;

    @Override
    public @NotNull String getIdentifier() {
        return "hmcpets";
    }

    @Override
    public @NotNull String getAuthor() {
        return "AlicsMacs";
    }

    @Override
    public @NotNull String getVersion() {
        return instance.getPluginMeta().getVersion();
    }


    @Override
    public String onPlaceholderRequest(Player player, @NonNull String identifier){
        UserModel model = userCache.get(player.getUniqueId());

        if(model == null) return null;

        return switch (identifier.toLowerCase()){
            case "ping" -> "pong";
            case "pet_points" -> Integer.toString(model.petPoints());
            default -> null;
        };
    }
}
