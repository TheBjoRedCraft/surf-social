package dev.slne.surf.friends.api.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@AllArgsConstructor
@Getter
public class FriendAddEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();

    private UUID player;
    private UUID target;

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }
}
