package dev.slne.surf.friends.command.subcommand;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.OfflinePlayerArgument;
import dev.jorel.commandapi.arguments.SafeSuggestions;
import dev.slne.surf.friends.FriendManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class FriendAcceptCommand extends CommandAPICommand {
    public FriendAcceptCommand(String name) {
        super(name);

        withArguments(new OfflinePlayerArgument("target").replaceSafeSuggestions(SafeSuggestions.suggest(info -> Bukkit.getOnlinePlayers().toArray(new Player[0]))));

        executesPlayer((player, args) -> {
            OfflinePlayer target = args.getUnchecked("target");

            if (target == null) {
                throw CommandAPI.failWithString("Der Spieler wurde nicht gefunden.");
            }

            if(!FriendManager.instance().hasFriendRequest(player.getUniqueId(), target.getUniqueId())){
                throw CommandAPI.failWithString("Du hast keine Freundschaftsanfrage von " + target.getName());
            }

            FriendManager.instance().acceptFriendRequests(player.getUniqueId(), target.getUniqueId());
        });
    }
}
