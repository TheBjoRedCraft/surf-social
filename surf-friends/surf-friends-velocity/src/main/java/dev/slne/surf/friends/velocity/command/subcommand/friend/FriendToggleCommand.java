package dev.slne.surf.friends.velocity.command.subcommand.friend;

import dev.jorel.commandapi.CommandAPICommand;

import dev.slne.surf.friends.api.FriendApi;
import dev.slne.surf.friends.velocity.VelocityInstance;

public class FriendToggleCommand extends CommandAPICommand {
    public FriendToggleCommand(String name) {
        super(name);

        executesPlayer((player, info)-> {
            VelocityInstance.getInstance().getApi().toggle(player.getUniqueId());
        });
    }
}
