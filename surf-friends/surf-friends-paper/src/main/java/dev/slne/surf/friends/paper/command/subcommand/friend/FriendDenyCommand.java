package dev.slne.surf.friends.paper.command.subcommand.friend;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.slne.surf.friends.paper.FriendsPaperPlugin;
import java.util.UUID;
import org.bukkit.Bukkit;

public class FriendDenyCommand extends CommandAPICommand {
    public FriendDenyCommand(String name) {
        super(name);

        withArguments(new StringArgument("player"));

        executesPlayer((player, info)-> {
            UUID target = Bukkit.getOfflinePlayer(String.valueOf(info.getUnchecked("player"))).getUniqueId();

            FriendsPaperPlugin.instance().api().denyFriendRequest(player.getUniqueId(), target);
        });
    }
}
