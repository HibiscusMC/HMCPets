package com.hibiscusmc.hmcpets.api.event;

import com.hibiscusmc.hmcpets.api.model.PetModel;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
public class PetNameChangeEvent extends Event implements Cancellable {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final PetModel pet;

    @Setter
    private String newName;

    private boolean cancelled;

    public PetNameChangeEvent(@NotNull PetModel pet, String newName) {
        this.pet = pet;
        this.newName = newName;
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