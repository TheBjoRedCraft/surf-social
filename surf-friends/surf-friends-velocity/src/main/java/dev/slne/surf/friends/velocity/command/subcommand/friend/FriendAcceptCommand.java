package dev.slne.surf.friends.velocity.command.subcommand.friend;

import com.velocitypowered.api.proxy.Player;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPICommand;

import dev.slne.surf.friends.velocity.VelocityInstance;
import dev.slne.surf.friends.velocity.command.argument.PlayerArgument;

public class FriendAcceptCommand extends CommandAPICommand {
    public FriendAcceptCommand(String name) {
        super(name);

        withArguments(PlayerArgument.player("target"));
        executesPlayer((player, args) -> {
            Player target = PlayerArgument.getPlayer("target", args);

            if (target == null) {
                throw CommandAPI.failWithString("Der Spieler wurde nicht gefunden.");
            }

            VelocityInstance.instance().friendApi().acceptFriendRequest(player.getUniqueId(), target.getUniqueId());
        });
    }
}
