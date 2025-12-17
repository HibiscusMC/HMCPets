package com.hibiscusmc.hmcpets.api.event;

import com.hibiscusmc.hmcpets.api.model.PetModel;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class PetDespawnEvent extends Event implements Cancellable {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final Player player;
    private final PetModel pet;

    private boolean cancelled;

    public PetDespawnEvent(@NotNull Player player, @NotNull PetModel pet) {
        this.player = player;
        this.pet = pet;
    }

    @NotNull
    public Player player() {
        return player;
    }

    @NotNull
    public PetModel pet() {
        return pet;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    @NotNull
    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

}