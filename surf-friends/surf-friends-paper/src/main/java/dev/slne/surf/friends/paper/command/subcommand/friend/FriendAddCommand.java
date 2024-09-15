package dev.slne.surf.friends.paper.command.subcommand.friend;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.slne.surf.friends.paper.FriendsPaperPlugin;
import org.bukkit.Bukkit;

import java.util.UUID;

public class FriendAddCommand extends CommandAPICommand {
    public FriendAddCommand(String name) {
        super(name);

        withArguments(new StringArgument("player"));

        executesPlayer((player, info)-> {
            String target = info.getUnchecked("player");

            UUID playerUUID = player.getUniqueId();
            UUID targetUUID = Bukkit.getOfflinePlayer(target).getUniqueId();

            FriendsPaperPlugin.instance().api().addFriend(playerUUID, targetUUID);
        });
    }
}
