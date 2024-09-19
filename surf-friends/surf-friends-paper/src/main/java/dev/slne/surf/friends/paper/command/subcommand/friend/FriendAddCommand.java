package dev.slne.surf.friends.paper.command.subcommand.friend;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.slne.surf.friends.paper.FriendsPaperPlugin;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import org.bukkit.Bukkit;

import java.util.UUID;

public class FriendAddCommand extends CommandAPICommand {
    public FriendAddCommand(String name) {
        super(name);

        ObjectList<String> players = new ObjectArrayList<>();
        Bukkit.getOnlinePlayers().forEach(online -> players.add(online.getName()));

        withArguments(new StringArgument("player").replaceSuggestions(ArgumentSuggestions.strings(players)));

        executesPlayer((player, info)-> {
            String target = info.getUnchecked("player");

            UUID playerUUID = player.getUniqueId();
            UUID targetUUID = Bukkit.getOfflinePlayer(target).getUniqueId();

            FriendsPaperPlugin.instance().api().sendFriendRequest(playerUUID, targetUUID);
        });
    }
}
