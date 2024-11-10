package dev.slne.surf.friends.command.subcommand;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.OfflinePlayerArgument;
import dev.slne.surf.friends.FriendManager;

import org.bukkit.OfflinePlayer;

public class FriendAddCommand extends CommandAPICommand {
    public FriendAddCommand(String name) {
        super(name);

        withArguments(new OfflinePlayerArgument("target"));

        executesPlayer((player, args)-> {
            OfflinePlayer target = args.getUnchecked("target");

            if (target == null) {
                throw CommandAPI.failWithString("Der Spieler wurde nicht gefunden.");
            }

            if(FriendManager.instance().hasFriendRequest(player.getUniqueId(), target.getUniqueId())){
                throw CommandAPI.failWithString("Du hast bereits Freundschaftsanfrage von " + target.getName());
            }

            if(FriendManager.instance().hasFriendRequest(target.getUniqueId(), player.getUniqueId())){
                throw CommandAPI.failWithString("Du hast bereits eine Freundschaftsanfrage an " + target.getName() + " gesendet.");
            }

            FriendManager.instance().sendFriendRequest(player.getUniqueId(), target.getUniqueId());
        });
    }
}
