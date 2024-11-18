package dev.slne.surf.friends.command.subcommand;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.OfflinePlayerArgument;
import dev.jorel.commandapi.arguments.SafeSuggestions;
import dev.slne.surf.friends.FriendManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class FriendRemoveCommand extends CommandAPICommand {
    public FriendRemoveCommand(String name) {
        super(name);
        withArguments(new OfflinePlayerArgument("target").replaceSafeSuggestions(SafeSuggestions.suggest(info -> Bukkit.getOnlinePlayers().toArray(new Player[0]))));

        executesPlayer((player, args)-> {
            OfflinePlayer target = args.getUnchecked("target");

            if (target == null) {
                throw CommandAPI.failWithString("Der Spieler wurde nicht gefunden.");
            }

            if(!FriendManager.instance().areFriends(player.getUniqueId(), target.getUniqueId())){
                throw CommandAPI.failWithString(String.format("Du bist nicht mit %s befreundet.", target.getName()));
            }

            FriendManager.instance().removeFriend(player.getUniqueId(), target.getUniqueId());
            FriendManager.instance().removeFriend(target.getUniqueId(), player.getUniqueId());
        });
    }
}
