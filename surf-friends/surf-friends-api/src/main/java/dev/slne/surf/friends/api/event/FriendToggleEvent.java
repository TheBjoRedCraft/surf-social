package dev.slne.surf.friends.api.event;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
@Getter
public class FriendToggleEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();

    private UUID player;
    private boolean from;
    private boolean to;

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }
}
