package dev.slne.surf.friends.velocity.command.subcommand.friend;

import com.velocitypowered.api.proxy.Player;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPICommand;

import dev.slne.surf.friends.velocity.VelocityInstance;
import dev.slne.surf.friends.velocity.command.argument.PlayerArgument;

import java.util.UUID;

public class FriendDenyCommand extends CommandAPICommand {
    public FriendDenyCommand(String name) {
        super(name);

        withArguments(PlayerArgument.player("target"));

        executesPlayer((player, args)-> {
            Player target = PlayerArgument.getPlayer("target", args);

            if (target == null) {
                throw CommandAPI.failWithString("Der Spieler wurde nicht gefunden.");
            }

            VelocityInstance.instance().friendApi().denyFriendRequest(player.getUniqueId(), target.getUniqueId());
        });
    }
}
