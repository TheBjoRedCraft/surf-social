package dev.slne.surf.friends.paper.command.subcommand.friend;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.slne.surf.friends.paper.FriendsPaperPlugin;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import java.util.UUID;
import org.bukkit.Bukkit;

public class FriendDenyCommand extends CommandAPICommand {
    public FriendDenyCommand(String name) {
        super(name);

        ObjectList<String> players = new ObjectArrayList<>();
        Bukkit.getOnlinePlayers().forEach(online -> players.add(online.getName()));

        withArguments(new StringArgument("player").replaceSuggestions(ArgumentSuggestions.strings(players)));

        executesPlayer((player, info)-> {
            UUID target = Bukkit.getOfflinePlayer(String.valueOf(info.getUnchecked("player"))).getUniqueId();

            FriendsPaperPlugin.instance().api().denyFriendRequest(player.getUniqueId(), target);
        });
    }
}
