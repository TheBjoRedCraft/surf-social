package dev.slne.surf.friends.paper.command.subcommand.friend;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.StringArgument;
import dev.slne.surf.friends.paper.FriendPlugin;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import java.util.UUID;
import org.bukkit.Bukkit;

public class FriendRemoveCommand extends CommandAPICommand {
    public FriendRemoveCommand(String name) {
        super(name);

        ObjectList<String> players = new ObjectArrayList<>();
        Bukkit.getOnlinePlayers().forEach(online -> players.add(online.getName()));

        withArguments(new StringArgument("player").replaceSuggestions(ArgumentSuggestions.strings(players)));

        executesPlayer((player, info)-> {
            UUID target = Bukkit.getOfflinePlayer((String) info.getUnchecked("player")).getUniqueId();

            FriendPlugin.instance().api().removeFriend(player.getUniqueId(), target);
            FriendPlugin.instance().api().removeFriend(target, player.getUniqueId());
        });
    }
}
